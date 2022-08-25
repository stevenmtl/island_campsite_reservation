package com.island.bookingapi.component;

import com.island.bookingapi.entity.ReservationEntity;
import com.island.bookingapi.enumtype.ReservationStatus;
import com.island.bookingapi.model.RequestCancellation;
import com.island.bookingapi.repository.InventoryRepository;
import com.island.bookingapi.repository.ReservationRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingCancellation {

    private final ReservationRespository reservationRespository;
    private final InventoryRepository inventoryRepository;

    @Retryable(maxAttempts = 3, value = RuntimeException.class, backoff = @Backoff(delay = 1000, multiplier = 2))
    public ReservationEntity cancelReservation(RequestCancellation requestCancellation){
        //1.fetch the reservation record by reservation id and email
        //2.mark the reservation as cancelled
        //3.increase the free site num between those reserved dates
        //If concurrent data inconsistency occurs, this method will be retried;
        //if it still fails for 3 times, the exception will throw via http response.
        String errorMsg;
        var previousReservationId = requestCancellation.getPreviousReservationId();
        var bookingEmail = requestCancellation.getEmail();

        var previousReservationEntity = reservationRespository.findReservationEntityByReservationId(previousReservationId);
        if( previousReservationEntity != null ){

            if( previousReservationEntity.getStatus() == ReservationStatus.CANCELLED ){
                errorMsg = "This reservation id: " + previousReservationId + " was already cancelled for email: " + bookingEmail;
                log.info(errorMsg);
                previousReservationEntity.setCancellErrorMsg(errorMsg);
                return previousReservationEntity;
            }else if( !previousReservationEntity.getEmail().equalsIgnoreCase(bookingEmail) ){
                errorMsg = "A correct original email is needed for cancellation for this reservation id: " + previousReservationId + " since " + bookingEmail +" doesn't match with original email!" ;
                log.info(errorMsg);
                previousReservationEntity.setCancellErrorMsg(errorMsg);
                return previousReservationEntity;
            }else{
                //good to cancel it now
                return completeCancellation(previousReservationEntity);
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
    protected ReservationEntity completeCancellation(ReservationEntity previousReservationEntity){

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

}
