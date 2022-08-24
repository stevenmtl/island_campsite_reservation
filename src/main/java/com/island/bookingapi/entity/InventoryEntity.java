package com.island.bookingapi.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "INVENTORY_TB")
public class InventoryEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int inventoryId;
    @Builder.Default
    private String siteName = "Island_Campsite";
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate stayDate;
    private int freeSiteNumber;
    @Version
    @Column(name = "OPTLOCK")
    private int version;
}
