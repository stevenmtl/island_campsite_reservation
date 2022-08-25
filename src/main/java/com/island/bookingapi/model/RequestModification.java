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
public class RequestModification extends RequestReservation {
    @NotNull(message = "The previous reservation Id cannot be null")
    private int previousReservationId;
}
