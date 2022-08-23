package com.island.bookingapi.repository;

import com.island.bookingapi.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface InventoryRepository extends JpaRepository<InventoryEntity, Integer> {
    List<InventoryEntity> findInventoryEntitiesByStayDateBetween(LocalDate startDate, LocalDate endDate);
}
