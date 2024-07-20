package com.forero.parking.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "history",
        indexes = {
                @Index(name = "parkingLotId_vehicleId_entranceDate_index", columnList = "parking_lot_id, vehicle_id, " +
                        "entrance_date", unique = true)
        }
)
public class HistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parking_lot_id")
    private ParkingLotEntity parkingLot;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    private VehicleEntity vehicle;

    @Column(name = "entrance_date")
    private LocalDateTime entranceDate;

    @Column(name = "departure_date")
    private LocalDateTime departureDate;

    @Column(name = "total_paid")
    private BigDecimal totalPaid;
}
