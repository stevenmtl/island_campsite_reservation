package com.island.bookingapi.component;

import com.island.bookingapi.builder.ReservationEntityBuilder;
import com.island.bookingapi.entity.InventoryEntity;
import com.island.bookingapi.entity.ReservationEntity;
import com.island.bookingapi.enumtype.ReservationStatus;
import com.island.bookingapi.model.RequestCancellation;
import com.island.bookingapi.model.RequestModification;
import com.island.bookingapi.model.RequestReservation;
import com.island.bookingapi.repository.InventoryRepository;
import com.island.bookingapi.repository.ReservationRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.nio.channels.ScatteringByteChannel;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class Booking {

    private final ReservationRespository reservationRespository;
    private final InventoryRepository inventoryRepository;
    private final ReservationEntityBuilder reservationEntityBuilder;

    /**
     * Place available campsites for next month starting from tomorrow .
     *
     * <p>
     * This method can be called from endpoint by an admin. For demo purpose,
     * this will be called automatically by this REST API when it starts
     *
     * @param  startDate  start date when campsites can be added in inventory
     * @param  endDate end date (inclusive) when campsites can be added in inventory
     * @param  numOfSites  the available number of campsites for each date ( putting the same is to simplify this demo)
     * @return      a list of InventoryEntities that are added in the inventory
     * @see         InventoryEntity
     */
    public List<InventoryEntity> initInventory(LocalDate startDate, LocalDate endDate, int numOfSites){
        var newInventory = startDate.datesUntil(endDate.plusDays(1))
                .map(d -> InventoryEntity.builder()
                        .siteName("OceanView Site")
                        .stayDate(d)
                        .freeSiteNumber(numOfSites)
                        .updateDT(LocalDateTime.now())
                .build())
                .collect(Collectors.toList());

        var savedInventory = inventoryRepository.saveAllAndFlush(newInventory);
        return savedInventory;
    }

    /**
     * reserve campsites for a user during specified checkin/checkout dates with the number of guests.
     * <p>
     * This method can be called from endpoint by an admin. For demo purpose,
     * this will be called automatically by this REST API when it starts
     *
     * @param  requestReservation  a reservation request sent from users via endpoint
     * @return      a ReservationEntity that includes all reservation info such as reservactionID,etc
     * @see         ReservationEntity
     */
    @Retryable(maxAttempts = 3, value = RuntimeException.class, backoff = @Backoff(delay = 1000, multiplier = 2))
    public ReservationEntity reserveCampsite(RequestReservation requestReservation){
        //functions to be implemented as below:
        //check if there are enough free sites between checkinDate and checkoutDate from Inventory
        //if yes, we need to reduce the free site num in inventory
        //then insert a new reservation
        //finally, a reservation id is returned if free site num is enough; otherwise, this reservation failed and return
        //error code/msg to client side via controller
        var newReservation = reservationEntityBuilder.apply(requestReservation);
        var startDate = requestReservation.getStartDate();
        var endDate = requestReservation.getEndDate();
        String errorMsg;

        //max 3 days can be reserved for each booking
        var bookingDays = ChronoUnit.DAYS.between(startDate,endDate);

        if ( bookingDays > 3 ) {
            newReservation.setStatus(ReservationStatus.FAIL);
            errorMsg = "The total number of days should be <= 3 days.";
            newReservation.setBookingErrorMsg(errorMsg);
            log.info(errorMsg);
            return newReservation;
        }

        //booking date should be between tomorrow and up to 1 month
        if( startDate.isBefore(LocalDate.now().plusDays(1)) || endDate.isAfter(LocalDate.now().plusMonths(1)) ){
            newReservation.setStatus(ReservationStatus.FAIL);
            newReservation.setBookingErrorMsg("The date ranges of booking should be betwee " + LocalDate.now().plusDays(1)
            + " and " + LocalDate.now().plusMonths(1));
            return newReservation;
        }

        return makeReservation(startDate,endDate,newReservation);
    }

    @Transactional
    protected ReservationEntity makeReservation(LocalDate startDate, LocalDate endDate, ReservationEntity newReservation)  {
        //retrieve all inventories between booking dates
        var toBeBookedCampSites = inventoryRepository.findInventoryEntitiesByStayDateBetween(startDate,endDate);
        log.info("Current inventory between {} and {}", startDate,endDate);
        toBeBookedCampSites.stream().forEach(System.out::println);

        //check if there are enough campsites on to be reserved dates for this reservation
        var isMissingSites = toBeBookedCampSites.stream().anyMatch(e -> e.getFreeSiteNumber() < newReservation.getNumOfGuests());

        if(!isMissingSites){
            toBeBookedCampSites
                    .stream()
                    .forEach(inventory -> {
                        inventory.setFreeSiteNumber(inventory.getFreeSiteNumber() - newReservation.getNumOfGuests());
                        inventory.setUpdateDT(LocalDateTime.now());
                    });
            newReservation.setStatus(ReservationStatus.RESERVED);
            newReservation.setBookingDT(LocalDateTime.now());

            //Note: this is to demo on how optimistic locking works
            if(newReservation.getName().contains("slow_transaction_demo")){
                //sleep 30s. If another user books a same date range during this time,
                //below inventoryRepository.saveAllAndFlush should throw Optimistic exception.
                try {
                    Thread.sleep(30000);
                }catch (InterruptedException ie)
                {
                    log.warn("InterruptedException occurred when sleeping!");
                }
            }

            inventoryRepository.saveAllAndFlush(toBeBookedCampSites);
            var savedRE = reservationRespository.save(newReservation);

            log.info("Current reservation is booked successfully with below info: ");
            System.out.println(savedRE.toString());

            return savedRE;
        }else{
            newReservation.setStatus(ReservationStatus.FAIL);
            newReservation.setBookingErrorMsg("Reservation failed since Not enough camp sites during booked dates.");
            log.info("Reservation failed since Not enough camp sites during booked dates.");
            return newReservation;
        }
    }

    @Retryable(maxAttempts = 3, value = OptimisticLockException.class, backoff = @Backoff(delay = 1000, multiplier = 2))
    public ReservationEntity cancelReservation(RequestCancellation requestCancellation){
        //1.fetch the reservation record by reservation id and email
        //2.mark the reservation as cancelled
        //3.increase the free site num between those reserved dates
        //If concurrent data inconsistency occurs, this method will be retried;
        //if still failing 3 times, the exception will throw via http response.
        String errorMsg;
        var previousReservationId = requestCancellation.getPreviousReservationId();
        var bookingEmail = requestCancellation.getEmail();

        var previousReservationEntity = reservationRespository.findReservationEntityByReservationId(previousReservationId);
        if( previousReservationEntity != null ){

            if( previousReservationEntity.getStatus() == ReservationStatus.CANCELLED ){
                errorMsg = "This reservation id: " + previousReservationId + " was already cancelled for email : !" + bookingEmail;
                log.info(errorMsg);
                previousReservationEntity.setCancellErrorMsg(errorMsg);
                return previousReservationEntity;
            }else if( !previousReservationEntity.getEmail().equalsIgnoreCase(bookingEmail) ){
                errorMsg = "This reservation id: " + previousReservationId + " doesn't match with email : !" + bookingEmail;
                log.info(errorMsg);
                previousReservationEntity.setCancellErrorMsg(errorMsg);
                return previousReservationEntity;
            }else{
                //good to cancel it now
                return makeCancellation(previousReservationEntity);
            }
        }else{

            errorMsg = "This reservation id: " + previousReservationId + " doesn't exist in our system";
            log.warn(errorMsg);
            return ReservationEntity.builder()
                    .reservationId(previousReservationId)
                    .email(bookingEmail)
                    .cancellErrorMsg(errorMsg)
                    .build();
        }

    }

    @Transactional
    protected ReservationEntity makeCancellation(ReservationEntity previousReservationEntity){

        var checkinDate = previousReservationEntity.getCheckinDate();
        var checkoutDate = previousReservationEntity.getCheckoutDate();
        var numOfGuests = previousReservationEntity.getNumOfGuests();
        var inventoriesToBeReleased = inventoryRepository.findInventoryEntitiesByStayDateBetween(checkinDate,checkoutDate);
        inventoriesToBeReleased.stream().forEach(e -> {
            e.setFreeSiteNumber(e.getFreeSiteNumber() + numOfGuests );
            e.setUpdateDT(LocalDateTime.now());
        });
        previousReservationEntity.setStatus(ReservationStatus.CANCELLED);
        previousReservationEntity.setUpdateDT(LocalDateTime.now());

        inventoryRepository.saveAllAndFlush(inventoriesToBeReleased);
        reservationRespository.saveAndFlush(previousReservationEntity);
        return previousReservationEntity;
    }

    public int ModifyReservation(RequestModification requestModification){
        //1.get the previous reservation id
        //2. cancel the previous one
        //3. reserve a new one
        //4. return a new reservation



        return 0;

    }

    public List<InventoryEntity> getFreeCampsites(LocalDate startDate, LocalDate endDate){

        if(startDate == null || startDate.isBefore(LocalDate.now().plusDays(1))) startDate = LocalDate.now().plusDays(1);
        if(endDate == null || endDate.isAfter(LocalDate.now().plusMonths(1))) endDate = LocalDate.now().plusMonths(1);
        return inventoryRepository.findInventoryEntitiesByStayDateBetween(startDate, endDate);
    }
}
