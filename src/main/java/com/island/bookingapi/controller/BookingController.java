package com.island.bookingapi.controller;

import com.island.bookingapi.component.Booking;
import com.island.bookingapi.model.RequestReservation;
import com.island.bookingapi.model.RequestDates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final Booking booking;



    @PostMapping(path = "/initInventory")
    public ResponseEntity<Object> initInventory(@Validated @RequestBody RequestDates requestDates, BindingResult bindingResult){
        log.info("Init inventory request with below info: {}", requestDates.toString());
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage));
        }
        return ResponseEntity.ok().body(booking.initInventory(requestDates.getStartDate(),requestDates.getEndDate(), 100));
    }

    @PostMapping(path = "/getFreeCampSites")
    public ResponseEntity<Object> getFreeCampSite(@Validated @RequestBody RequestDates requestDates, BindingResult bindingResult){
        log.info("A new reservation request with below info: {}", requestDates.toString());
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage));
        }

        return ResponseEntity.ok().body(booking.getFreeCampsites(requestDates.getStartDate(), requestDates.getEndDate()));

    }

    @PostMapping(path = "/reserveCampsite")
    public ResponseEntity<Object> reserveCampSite(@Validated @RequestBody RequestReservation requestReservation, BindingResult bindingResult){
        log.info("A new reservation request with below info: {}", requestReservation.toString());
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage));
        }
        return ResponseEntity.ok().body(booking.reserveCampsite(requestReservation));
    }


}
