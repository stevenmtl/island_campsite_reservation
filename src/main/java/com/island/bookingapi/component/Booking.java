package com.island.bookingapi.component;

import com.island.bookingapi.builder.ReservationEntityBuilder;
import com.island.bookingapi.entity.InventoryEntity;
import com.island.bookingapi.entity.ReservationEntity;
import com.island.bookingapi.enumtype.ReservationStatus;
import com.island.bookingapi.model.RequestCancellation;
import com.island.bookingapi.model.RequestReservation;
import com.island.bookingapi.repository.InventoryRepository;
import com.island.bookingapi.repository.ReservationRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.OptimisticLockException;
import java.nio.channels.ScatteringByteChannel;
import java.time.LocalDate;
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

    public List<InventoryEntity> initInventory(LocalDate startDate, LocalDate endDate, int numOfSites){
        var newInventory = startDate.datesUntil(endDate.plusDays(1))
                .map(d -> InventoryEntity.builder()
                        .siteName("OceanView Site")
                        .stayDate(d)
                        .freeSiteNumber(numOfSites)
                .build())
                .collect(Collectors.toList());

        var savedInventory = inventoryRepository.saveAllAndFlush(newInventory);
        return savedInventory;
    }


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

        //max 3 days can be reserved for each booking
        var bookingDays = ChronoUnit.DAYS.between(startDate,endDate);

        if ( bookingDays > 3 ) {
            newReservation.setStatus(ReservationStatus.FAIL);
            newReservation.setBookingErrorMsg("The total number of days should be <= 3 days.");
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
    @Retryable(maxAttempts = 3, value = OptimisticLockException.class, backoff = @Backoff(delay = 1000, multiplier = 2))
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
                    .forEach(inventory -> inventory.setFreeSiteNumber(inventory.getFreeSiteNumber() - newReservation.getNumOfGuests()));
            newReservation.setStatus(ReservationStatus.RESERVED);

            var savedRE = reservationRespository.save(newReservation);

            //Note: this is to demo how optimistic locking works
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

            log.info("Current reservation is booked successfully with below info: ");
            System.out.println(savedRE.toString());

            return savedRE;
        }else{
            newReservation.setStatus(ReservationStatus.FAIL);
            newReservation.setBookingErrorMsg("Not enough camp sites during booked dates.");
            return newReservation;
        }
    }

    public boolean cancelReservation(RequestCancellation requestCancellation){
        //1.fetch the reservation record by reservation id and email
        //2.mark the reservation as cancelled
        //3.increase the free site num between those reserved dates



        return false;
    }

    public int ModifyReservation(RequestReservation requestReservation){
        //1.get the previous reservation id
        //2. cancel the previous one
        //3. reserve a new one
        //4. return a new reservation id
        return 0;

    }

    public List<InventoryEntity> getFreeCampsites(LocalDate startDate, LocalDate endDate){

        if(startDate == null || startDate.isBefore(LocalDate.now().plusDays(1))) startDate = LocalDate.now().plusDays(1);
        if(endDate == null || endDate.isAfter(LocalDate.now().plusMonths(1))) endDate = LocalDate.now().plusMonths(1);
        return inventoryRepository.findInventoryEntitiesByStayDateBetween(startDate, endDate);
    }
}
