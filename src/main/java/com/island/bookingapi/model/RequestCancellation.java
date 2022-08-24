package com.island.bookingapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class RequestCancellation {

    @NotNull(message = "The reservationId cannot be null")
    private int previousReservationId;
    @NotNull(message = "email cannot be null")
    private String email;
}


