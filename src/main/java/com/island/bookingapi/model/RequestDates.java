package com.island.bookingapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Validated
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class RequestDates {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "startDate cannot be null")
    private LocalDate startDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "endDate cannot be null")
    private LocalDate endDate;
}
