package com.island.bookingapi.controller;

import com.island.bookingapi.component.Booking;
import com.island.bookingapi.model.RequestConfig;
import com.island.bookingapi.model.RequestDates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
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

    @PostMapping(path = "/ReserveCampsite")
    public ResponseEntity<Object> reserveCampSite(@Validated @RequestBody RequestConfig requestConfig, BindingResult bindingResult){
        log.info("A new reservation request with below info: {}", requestConfig.toString());
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage));
        }
        return ResponseEntity.ok().body(booking.reserveCampsite(requestConfig));
    }

    @PostMapping(path = "/getFreeCampSites")
    public ResponseEntity<Object> reserveCampSite(@Validated @RequestBody RequestDates requestDates, BindingResult bindingResult){
        log.info("A new reservation request with below info: {}", requestDates.toString());
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage));
        }

        return ResponseEntity.ok().body(booking.getFreeCampsites(requestDates.getCheckinDate(), requestDates.getCheckoutDate()));

    }

}
