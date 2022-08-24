package com.island.bookingapi.service;

import com.island.bookingapi.model.RequestReservation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookingService {

    public ResponseEntity<Object> reserveCampSite(RequestReservation requestReservation){
        return ResponseEntity.ok().body("Ok");
    }
}
