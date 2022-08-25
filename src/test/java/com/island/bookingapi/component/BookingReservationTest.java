package com.island.bookingapi.component;

import com.island.bookingapi.builder.ReservationEntityBuilder;
import com.island.bookingapi.model.RequestReservation;
import com.island.bookingapi.predicate.ReservationDateRangePredicate;
import com.island.bookingapi.repository.InventoryRepository;
import com.island.bookingapi.repository.ReservationRespository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingReservationTest {

    @Spy
    @InjectMocks
    private BookingReservation bookingReservation;


    @Mock
    private  ReservationRespository reservationRespository;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private  ReservationEntityBuilder reservationEntityBuilder;
    @Mock
    private ReservationDateRangePredicate reservationDateRangePredicate;



    @Test
    void bookReservation() {



        var requestReservation = RequestReservation.builder()
                .email("steven.mtl@gmail.com")
                .name("Steven Li")
                .numOfGuests(10)
                .startDate(LocalDate.of(2022,9,1))
                .endDate(LocalDate.of(2022,9,2))
                .build();

        var reservationEntity = new ReservationEntityBuilder().apply(requestReservation);

        when(reservationEntityBuilder.apply(requestReservation)).thenReturn(reservationEntity);
        when(reservationRespository.save(any())).thenReturn(reservationEntity.toBuilder().reservationId(1).build());


        var result = bookingReservation.bookReservation(requestReservation);
        verify(inventoryRepository,times(1)).saveAllAndFlush(any());
    }
}