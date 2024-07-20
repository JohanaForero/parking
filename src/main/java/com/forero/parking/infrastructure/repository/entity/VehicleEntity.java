package com.forero.parking.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "vehicle",
        indexes = {
                @Index(name = "licensePlate_index", columnList = "license_plate", unique = true)
        }
)
public class VehicleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate")
    private String licensePlate;
}
