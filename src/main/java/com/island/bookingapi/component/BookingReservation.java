package com.island.bookingapi.component;

import com.island.bookingapi.builder.ReservationEntityBuilder;
import com.island.bookingapi.entity.ReservationEntity;
import com.island.bookingapi.enumtype.ReservationStatus;
import com.island.bookingapi.model.RequestReservation;
import com.island.bookingapi.repository.InventoryRepository;
import com.island.bookingapi.repository.ReservationRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingReservation {

    private final ReservationRespository reservationRespository;
    private final InventoryRepository inventoryRepository;
    private final ReservationEntityBuilder reservationEntityBuilder;

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
    public ReservationEntity bookReservation(RequestReservation requestReservation){
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

        return completeReservation(startDate,endDate,newReservation);
    }

    @Transactional
    protected ReservationEntity completeReservation(LocalDate startDate, LocalDate endDate, ReservationEntity newReservation)  {
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

}
