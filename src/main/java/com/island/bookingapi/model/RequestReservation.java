package com.island.bookingapi.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
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
