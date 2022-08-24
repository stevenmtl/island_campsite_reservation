package com.island.bookingapi;

import com.island.bookingapi.component.Booking;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.util.Arrays;

@SpringBootApplication
@Slf4j
public class BookingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingApiApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            log.info("Init our inventory for demo purpose:");
            var booking = (Booking)ctx.getBean(Booking.class);
            var inventories = booking.initInventory(LocalDate.now().plusDays(1),LocalDate.now().plusMonths(1), 100);
            log.info("Below campsites have been added in inventory:");
            inventories.stream().forEach(System.out::println);
        };
    }

}
