package com.island.bookingapi.model;


import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@SuperBuilder
@Validated
@NoArgsConstructor
@AllArgsConstructor
public class RequestReservation extends RequestDates {

    @NotNull(message = "email cannot be null")
    private String email;
    @NotNull(message = "name cannot be null")
    private String name;
    @NotNull(message = "The number of guests cannot be null")
    private int numOfGuests;

}
