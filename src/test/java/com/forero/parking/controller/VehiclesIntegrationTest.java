package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.ParkingVehiclesResponseDto;
import com.forero.parking.openapi.model.VehicleDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

class VehiclesIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/parking/vehicles";

    private int parkingId(final String name) {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking WHERE name = ?",
                Integer.class, name);
        return id != null ? id : 0;
    }

    private int parkingLotId(final String entrance_date) {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking WHERE entrance_date = ?",
                Integer.class, entrance_date);
        return id != null ? id : 0;
    }

    private int vehicleId(final String licensePlate) {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM vehicle WHERE license_plate = ?",
                Integer.class, licensePlate);
        return id != null ? id : 0;
    }

    @Test
    void test_getVehiclesInParkingAndRoleAdmin_withValidData_shouldReturnParkingVehicleResponseDto() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "test16", 1200, 80);
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD123");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD124");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD125");
        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141041", this.parkingId("test16"), 2);
        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141042", this.parkingId("test16"), 12);
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId("2024-07-20 15:31:11.141041"),
                this.vehicleId("ABD123"), "2024-07-20 15:31:11.141041");
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId("2024-07-20 15:31:11.141042"),
                this.vehicleId("ABD124"), "2024-07-20 15:31:11.141042");
        final VehicleDto vehicleDto = new VehicleDto();
        final long vehicleId = this.vehicleId("ABD123");
        final long parkingLot = this.parkingLotId("2024-07-20 15:31:11.141041");
        vehicleDto.id(vehicleId);
        vehicleDto.licensePlate("ABD123");
        vehicleDto.parkingLotId(parkingLot);

        final VehicleDto vehicleDto2 = new VehicleDto();
        final long vehicleId2 = this.vehicleId("ABD124");
        final long parkingLot2 = this.parkingLotId("2024-07-20 15:31:11.141042");
        vehicleDto2.id(vehicleId2);
        vehicleDto2.licensePlate("ABD124");
        vehicleDto2.parkingLotId(parkingLot2);
        vehicleDto2.entranceDate(OffsetDateTime.parse("2024-07-20 15:31:11.141042"));
        final List<VehicleDto> vehicleDtos = new ArrayList<>();
        vehicleDtos.add(vehicleDto);
        vehicleDtos.add(vehicleDto2);
        final ParkingVehiclesResponseDto expected = new ParkingVehiclesResponseDto();
        expected.setVehicles(vehicleDtos);

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.get(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ParkingVehiclesResponseDto actual = OBJECT_MAPPER.readValue(body, ParkingVehiclesResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
