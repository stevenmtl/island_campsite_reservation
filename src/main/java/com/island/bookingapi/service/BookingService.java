package com.island.bookingapi.service;

import com.island.bookingapi.component.BookingCancellation;
import com.island.bookingapi.component.BookingModification;
import com.island.bookingapi.component.BookingReservation;
import com.island.bookingapi.model.RequestCancellation;
import com.island.bookingapi.model.RequestModification;
import com.island.bookingapi.model.RequestReservation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingReservation bookingReservation;
    private final BookingCancellation bookingCancellation;
    private final BookingModification bookingModification;

    public ResponseEntity<Object> bookingReservation(RequestReservation requestReservation){
        log.info("Start a new reservation:");
        return ResponseEntity.ok().body(bookingReservation.bookReservation(requestReservation));
    }

    public ResponseEntity<Object> bookingCancellation(RequestCancellation requestCancellation){
        log.info("Start a new reservation:");
        return ResponseEntity.ok().body(bookingCancellation.cancelReservation(requestCancellation));
    }

    public ResponseEntity<Object> bookingModification(RequestModification requestModification){
        log.info("Start a new reservation:");
        return ResponseEntity.ok().body(bookingModification.modifyReservation(requestModification));
    }




}
