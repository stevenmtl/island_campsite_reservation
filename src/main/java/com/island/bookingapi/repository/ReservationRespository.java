package com.island.bookingapi.repository;

import com.island.bookingapi.entity.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRespository extends JpaRepository<ReservationEntity, Integer> {
    List<ReservationEntity> findReservationEntityByReservationId(int reservationId);
}
