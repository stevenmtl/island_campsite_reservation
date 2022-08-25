package com.island.bookingapi.service;

import com.island.bookingapi.entity.InventoryEntity;
import com.island.bookingapi.repository.InventoryRepository;
import com.island.bookingapi.repository.ReservationRespository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MaintenanceService {

    @Value("${booking.inventory.totalcampsites}")
    private int initTotalCampsites;
    private final ReservationRespository reservationRespository;
    private final InventoryRepository inventoryRepository;

    /**
     * Place available campsites for next month starting from tomorrow .
     *
     * <p>
     * This method can be called from endpoint by an admin. For demo purpose,
     * this will be called automatically by this REST API when it starts
     *
     * @param  startDate  start date when campsites can be added in inventory
     * @param  endDate end date (inclusive) when campsites can be added in inventory
     * @param  numOfSites  the available number of campsites for each date ( putting the same is to simplify this demo)
     * @return      a list of InventoryEntities that are added in the inventory
     * @see         InventoryEntity
     */
    @Transactional
    public List<InventoryEntity> initInventory(LocalDate startDate, LocalDate endDate, int numOfSites){
        var newInventory = startDate.datesUntil(endDate.plusDays(1))
                .map(d -> InventoryEntity.builder()
                        .siteName("OceanView Site")
                        .stayDate(d)
                        .freeSiteNumber(numOfSites)
                        .updateDT(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());

        reservationRespository.deleteAll();
        inventoryRepository.deleteAll();
        var savedInventory = inventoryRepository.saveAllAndFlush(newInventory);
        return savedInventory;
    }

    public List<InventoryEntity> getFreeCampsites(LocalDate startDate, LocalDate endDate){

        if(startDate == null || startDate.isBefore(LocalDate.now().plusDays(1))) startDate = LocalDate.now().plusDays(1);
        if(endDate == null || endDate.isAfter(LocalDate.now().plusMonths(1))) endDate = LocalDate.now().plusMonths(1);
        return inventoryRepository.findInventoryEntitiesByStayDateBetween(startDate, endDate);
    }


}
