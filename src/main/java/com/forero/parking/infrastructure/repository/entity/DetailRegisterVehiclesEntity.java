package com.forero.parking.infrastructure.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
//@Entity
//@Table(name = "detailRegisterVehicles",
//        indexes = {
//                @Index(name = "parkingLotId_vehicleId_entranceDate_index", columnList = "parking_lot_id, vehicle_id, " +
//                        "entrance_date", unique = true)
//        }
//)
@Entity
@Table(name = "Detail_register_vehicles")
public class DetailRegisterVehiclesEntity {
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
}
