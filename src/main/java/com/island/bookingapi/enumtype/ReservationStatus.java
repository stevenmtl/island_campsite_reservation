package com.island.bookingapi.enumtype;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@AllArgsConstructor
public enum ReservationStatus {
    RESERVED("RESERVED"),
    CHECKIN("CHECKIN"),
    CHECKOUT("CHECKOUT"),
    CANCELLED("CANCELLED");

    @Getter
    String value;

}
