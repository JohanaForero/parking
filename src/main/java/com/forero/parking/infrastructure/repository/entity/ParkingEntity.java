package com.forero.parking.infrastructure.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "parking")
public class ParkingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "partner_id")
    private String partnerId;

    @Column(name = "name")
    private String parkingName;

    @Column(name = "cost_per_hour")
    private int costPerHour;

    @Column(name = "number_of_parking_lots")
    private int numberOfParkingLots;
}
