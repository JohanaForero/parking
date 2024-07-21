package com.forero.parking.infrastructure.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "parking_lot",
        indexes = {
                @Index(name = "vehicleId_index", columnList = "vehicle_id", unique = true)
        }
)
public class ParkingLotEntity {
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicle;

    @Column(name = "entrance_date")
    private LocalDateTime entranceDate;
}
