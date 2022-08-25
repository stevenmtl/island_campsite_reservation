package com.island.bookingapi.predicate;

import com.island.bookingapi.model.RequestReservation;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.function.Predicate;

@Component
public class ReservationDateRangePredicate implements Predicate<RequestReservation> {

    @Override
    public boolean test(RequestReservation requestReservation) {

        var startDate = requestReservation.getStartDate();
        var endDate = requestReservation.getEndDate();

        //max 3 days can be reserved for each booking
        var bookingDays = ChronoUnit.DAYS.between(startDate,endDate) + 1;
        return bookingDays > 3 ;
    }
}
