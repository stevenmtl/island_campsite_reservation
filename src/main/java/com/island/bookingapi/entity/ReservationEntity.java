package com.island.bookingapi.entity;

import com.island.bookingapi.enumtype.ReservationStatus;
import com.island.bookingapi.enumtype.StringToEnumConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;


@Entity
@Builder
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
    @Transient
    private String bookingErrorMsg;
    @Version
    @Column(name = "OPTLOCK")
    private int version;
}
