package com.forero.parking.infrastructure.repository.entity;

import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicle;

    @Column(name = "entrance_date")
    private LocalDateTime entranceDate;
}
