package com.island.bookingapi.entity;

import com.island.bookingapi.enumtype.ReservationStatus;
import com.island.bookingapi.enumtype.StringToEnumConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RESERVATION_TB")
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reservationId;
    private String email;
    private String name;
    private LocalDate checkinDate;
    private LocalDate checkoutDate;
    private int numOfGuests;
    @Convert(converter = StringToEnumConverter.class)
    private ReservationStatus status;
    private LocalDateTime bookingDT;
    private LocalDateTime updateDT;
    @Transient
    private String bookingErrorMsg;
    @Transient
    private String cancellErrorMsg;
    @Version
    @Column(name = "OPTLOCK")
    private int version;
}
