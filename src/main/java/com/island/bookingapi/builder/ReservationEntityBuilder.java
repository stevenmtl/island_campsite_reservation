package com.island.bookingapi.builder;

import com.island.bookingapi.entity.ReservationEntity;
import com.island.bookingapi.enumtype.ReservationStatus;
import com.island.bookingapi.model.RequestReservation;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ReservationEntityBuilder implements Function<RequestReservation, ReservationEntity> {
    @Override
    public ReservationEntity apply(RequestReservation requestReservation) {
        return ReservationEntity.builder()
                .email(requestReservation.getEmail())
                .name(requestReservation.getName())
                .checkinDate(requestReservation.getStartDate())
                .checkoutDate(requestReservation.getEndDate())
                .numOfGuests(requestReservation.getNumOfGuests())
                .status(ReservationStatus.RESERVED)
                .build();

    }
}
