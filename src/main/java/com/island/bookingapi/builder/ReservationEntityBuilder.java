package com.island.bookingapi.builder;

import com.island.bookingapi.entity.ReservationEntity;
import com.island.bookingapi.enumtype.ReservationStatus;
import com.island.bookingapi.model.RequestConfig;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class ReservationEntityBuilder implements Function<RequestConfig, ReservationEntity> {
    @Override
    public ReservationEntity apply(RequestConfig requestConfig) {
        return ReservationEntity.builder()
                .email(requestConfig.getEmail())
                .name(requestConfig.getName())
                .checkinDate(requestConfig.getCheckinDate())
                .checkoutDate(requestConfig.getCheckoutDate())
                .numOfGuests(requestConfig.getNumOfGuests())
                .status(ReservationStatus.RESERVED)
                .build();

    }
}
