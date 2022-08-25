package com.island.bookingapi.component;

import com.island.bookingapi.entity.ReservationEntity;
import com.island.bookingapi.model.RequestCancellation;
import com.island.bookingapi.model.RequestModification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingModification {

    private final BookingReservation bookingReservation;
    private final BookingCancellation bookingCancellation;
    @Transactional
    public ReservationEntity modifyReservation(RequestModification requestModification){
        //1. cancel the previous one
        //2. reserve a new one
        //3. return a new reservation

        var requestCancellation = RequestCancellation.builder()
                .previousReservationId(requestModification.getPreviousReservationId())
                .email(requestModification.getEmail())
                .build();

        var cancelledReservaction = bookingCancellation.cancelReservation(requestCancellation);
        var newReservation = bookingReservation.bookReservation(requestModification);

        return newReservation;
    }
}
