package com.island.bookingapi;

import com.island.bookingapi.service.MaintenanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;

import java.time.LocalDate;

@SpringBootApplication
@Slf4j
@EnableRetry
public class BookingApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingApiApplication.class, args);
    }
/*
    @Bean
    public List<RetryListener> retryListeners() {

        return Collections.singletonList(new RetryListenerSupport() {

            @Override
            public <T, E extends Throwable> void onError(
                    RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
                log.info("Retryable method {} threw {}th exception {}",
                        context.getAttribute("context.name"),
                        context.getRetryCount(), throwable.toString());
            }
        });
    }
*/
    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {

            log.info("Init our inventory for demo purpose:");
            var maintenanceService = (MaintenanceService)ctx.getBean(MaintenanceService.class);
            var inventories = maintenanceService.initInventory(LocalDate.now().plusDays(1),LocalDate.now().plusMonths(1), 100);
            log.info("Below campsites have been added in inventory:");
            inventories.stream().forEach(System.out::println);
        };
    }

}
