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
public class RequestDates {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "checkinDate cannot be null")
    private LocalDate checkinDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "checkoutDate cannot be null")
    private LocalDate checkoutDate;
}
