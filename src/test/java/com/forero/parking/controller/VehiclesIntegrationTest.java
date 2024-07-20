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
import java.util.List;

class VehiclesIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/parking/vehicles";

    @Test
    void test_getVehiclesInParking_withValidData_shouldReturnParkingVehicleResponseDto() throws Exception {
        //Given
        final String licensePlate = "nB99";
        final long occupiedParkingLotId = 1;
        this.jdbcTemplate.update("INSERT INTO vehicle (id, license_plate) VALUES (?, ?)", 1, licensePlate);
        this.jdbcTemplate.update("INSERT INTO parking_lot (id, vehicle_id, entrance_date) VALUES (?, ?, ?)", occupiedParkingLotId, 1,
                "2024-07-20 15:31:11.141046");
        this.jdbcTemplate.update("INSERT INTO history (id, parking_lot_id, vehicle_id, entrance_date) VALUES (?, ?, " +
                "?, ?)", 1, occupiedParkingLotId, 1, "2024-07-20 15:31:11.141046");

        final VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.id(1L);
        vehicleDto.licensePlate(licensePlate);
        vehicleDto.parkingLotId(occupiedParkingLotId);
        vehicleDto.entranceDate(OffsetDateTime.parse("2024-07-20T13:31:11.141046Z"));
        final ParkingVehiclesResponseDto expected = new ParkingVehiclesResponseDto();
        expected.setVehicles(List.of(vehicleDto));

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
