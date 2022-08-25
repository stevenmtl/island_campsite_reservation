package com.island.bookingapi.model;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data()
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class RequestModification extends RequestReservation {
    @NotNull(message = "The previous reservation Id cannot be null")
    private int previousReservationId;
}
