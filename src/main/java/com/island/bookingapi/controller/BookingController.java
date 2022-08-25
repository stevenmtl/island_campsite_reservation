package com.island.bookingapi.controller;

import com.island.bookingapi.service.BookingService;
import com.island.bookingapi.model.RequestCancellation;
import com.island.bookingapi.model.RequestModification;
import com.island.bookingapi.model.RequestReservation;
import com.island.bookingapi.model.RequestDates;
import com.island.bookingapi.service.MaintenanceService;
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

    private final BookingService bookingService;
    private final MaintenanceService maintenanceService;

    @PostMapping(path = "/initInventory")
    public ResponseEntity<Object> initInventory(@Validated @RequestBody RequestDates requestDates, BindingResult bindingResult){
        log.info("Init inventory request with below info: {}", requestDates.toString());
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage));
        }
        return ResponseEntity.ok().body(maintenanceService.initInventory(requestDates.getStartDate(),requestDates.getEndDate(), 100));
    }

    @PostMapping(path = "/getFreeCampSites")
    public ResponseEntity<Object> getFreeCampSite(@Validated @RequestBody RequestDates requestDates, BindingResult bindingResult){
        log.info("A new free campsite request with below info: {}", requestDates.toString());
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage));
        }
        return ResponseEntity.ok().body(maintenanceService.getFreeCampsites(requestDates.getStartDate(), requestDates.getEndDate()));
    }

    @PostMapping(path = "/bookReservation")
    public ResponseEntity<Object> bookReservation(@Validated @RequestBody RequestReservation requestReservation, BindingResult bindingResult){
        log.info("A new reservation request received with below info: {}", requestReservation.toString());
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage));
        }
        return bookingService.bookingReservation(requestReservation);
    }

    @PostMapping(path = "/cancelReservation")
    public ResponseEntity<Object> cancelReservation(@Validated @RequestBody RequestCancellation requestCancellation, BindingResult bindingResult){
        log.info("A new cancellation request received with below info: {}", requestCancellation.toString());
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage));
        }
        return bookingService.bookingCancellation(requestCancellation);
    }

    @PostMapping(path = "/modifyReservation")
    public ResponseEntity<Object> modifyReservation(@Validated @RequestBody RequestModification requestModification, BindingResult bindingResult){
        log.info("A new cancellation request received with below info: {}", requestModification.toString());
        if(bindingResult.hasErrors()){
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage));
        }
        return bookingService.bookingModification(requestModification);
    }

}
