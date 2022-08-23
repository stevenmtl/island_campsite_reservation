package com.island.bookingapi.component;

import com.island.bookingapi.builder.ReservationEntityBuilder;
import com.island.bookingapi.entity.InventoryEntity;
import com.island.bookingapi.model.RequestConfig;
import com.island.bookingapi.repository.InventoryRepository;
import com.island.bookingapi.repository.ReservationRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Booking {

    private final ReservationRespository reservationRespository;
    private final InventoryRepository inventoryRepository;
    private final ReservationEntityBuilder reservationEntityBuilder;

    public int reserveCampsite(RequestConfig requestConfig){
        //Summary of functions to be implemented here
        //check if there are enough free sites between checkinDate and checkoutDate from Inventory
        //if yes, we need to reduce the free site num in inventory
        //then insert a new reservation
        //finally, a reservation id is returned if free site num is enough; otherwise, this reservation failed and return
        //error code/msg to client side via controller
        var re = reservationEntityBuilder.apply(requestConfig);
        var savedRE = reservationRespository.save(re);

        return savedRE.getReservationId();

    }

    public boolean cancelReservation(RequestConfig requestConfig){
        //1.mark the reservation as cancelled
        //2. increase the free site num between those reserved dates
        return false;
    }

    public int ModifyReservation(RequestConfig requestConfig){
        //1.get the previous reservation id
        //2. cancel the previous one
        //3. reserve a new one
        //4. return a new reservation id
        return 0;

    }

    public List<InventoryEntity> getFreeCampsites(LocalDate startDate, LocalDate endDate){

        if(startDate == null || startDate.isBefore(LocalDate.now().plusDays(1))) startDate = LocalDate.now().plusDays(1);
        if(endDate == null || endDate.isAfter(LocalDate.now().plusMonths(1))) endDate = LocalDate.now().plusMonths(1);
        return inventoryRepository.findInventoryEntitiesByStayDateBetween(startDate, endDate);
    }
}
