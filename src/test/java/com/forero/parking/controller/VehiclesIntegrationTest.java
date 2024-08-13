package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.ErrorObjectDto;
import com.forero.parking.openapi.model.PaginationRequestDto;
import com.forero.parking.openapi.model.PaginationResponseDto;
import com.forero.parking.openapi.model.VehicleResponseDto;
import com.forero.parking.openapi.model.VehiclesRequestDto;
import com.forero.parking.openapi.model.VehiclesResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

class VehiclesIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/parking/vehicles";

    private int parkingId() {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking WHERE name = ?",
                Integer.class, "test16");
        return id != null ? id : 0;
    }

    private int parkingLotId(final int code) {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking_lot WHERE code = ?",
                Integer.class, code);
        return id != null ? id : 0;
    }

    private int vehicleId(final String licensePlate) {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM vehicle WHERE license_plate = ?",
                Integer.class, licensePlate);
        return id != null ? id : 0;
    }

    @Test
    void test_getVehiclesInParking_withValidDataAndRoleAdmin_shouldReturnParkingVehicleResponseDto() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "test16", 1200, 80);

        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD123");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD124");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141041", this.parkingId(), 2);
        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141042", this.parkingId(), 12);

        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(2),
                this.vehicleId("ABD123"), "2024-07-20 15:31:11.141041");
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(12),
                this.vehicleId("ABD124"), "2024-07-20 15:31:11.141042");

        final PaginationRequestDto paginationRequestDto = new PaginationRequestDto();
        paginationRequestDto.setPage(1);
        paginationRequestDto.setPageSize(1);

        final VehiclesRequestDto vehiclesRequestDto = new VehiclesRequestDto();
        vehiclesRequestDto.setParkingId(this.parkingId());
        vehiclesRequestDto.setPaginationRequest(paginationRequestDto);

        final VehicleResponseDto vehicleDto = new VehicleResponseDto();
        final long vehicleId = this.vehicleId("ABD124");
        vehicleDto.id(vehicleId);
        vehicleDto.licensePlate("ABD124");
        vehicleDto.code(12L);

        final List<VehicleResponseDto> vehicleDtos = new ArrayList<>();
        vehicleDtos.add(vehicleDto);

        final PaginationResponseDto paginationResponseDto = new PaginationResponseDto();
        paginationResponseDto.setTotal(2);
        paginationResponseDto.setTotalPages(2);

        final VehiclesResponseDto expected = new VehiclesResponseDto();
        expected.setVehicles(vehicleDtos);
        expected.setPaginationResponse(paginationResponseDto);

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(vehiclesRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        final String body = response.andReturn().getResponse().getContentAsString();
        final VehiclesResponseDto actual = OBJECT_MAPPER.readValue(body, VehiclesResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_getVehiclesInParking_withValidDataAndRolePartner_shouldReturnParkingVehicleResponseDto() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "test16", 1200, 80);

        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD123");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD124");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141041", this.parkingId(), 2);
        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141042", this.parkingId(), 12);

        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(2),
                this.vehicleId("ABD123"), "2024-07-20 15:31:11.141041");
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(12),
                this.vehicleId("ABD124"), "2024-07-20 15:31:11.141042");

        final PaginationRequestDto paginationRequestDto = new PaginationRequestDto();
        paginationRequestDto.setPage(1);
        paginationRequestDto.setPageSize(1);

        final VehiclesRequestDto vehiclesRequestDto = new VehiclesRequestDto();
        vehiclesRequestDto.setParkingId(this.parkingId());
        vehiclesRequestDto.setPaginationRequest(paginationRequestDto);

        final VehicleResponseDto vehicleDto = new VehicleResponseDto();
        final long vehicleId = this.vehicleId("ABD124");
        vehicleDto.id(vehicleId);
        vehicleDto.licensePlate("ABD124");
        vehicleDto.code(12L);

        final List<VehicleResponseDto> vehicleDtos = new ArrayList<>();
        vehicleDtos.add(vehicleDto);

        final PaginationResponseDto paginationResponseDto = new PaginationResponseDto();
        paginationResponseDto.setTotal(2);
        paginationResponseDto.setTotalPages(2);

        final VehiclesResponseDto expected = new VehiclesResponseDto();
        expected.setVehicles(vehicleDtos);
        expected.setPaginationResponse(paginationResponseDto);

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + PARTNER_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(vehiclesRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        final String body = response.andReturn().getResponse().getContentAsString();
        final VehiclesResponseDto actual = OBJECT_MAPPER.readValue(body, VehiclesResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_getVehiclesInParking_withParkingNotAssociatedWithMyPartnerRolePartner_shouldReturnUserNotFoundException() throws Exception {
        //Given
        final String token =
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6IklGdXFsT1JhbFhtcGlycWxfbnV5ODU5bDNrNk9tZVNVZlJPeTA1Z0dqd3MifQ.eyJleHAiOjE3MjMyNjQ3MTAsImlhdCI6MTcyMzI0MzExMCwianRpIjoiYjI0ODlhZDQtY2Y1MC00YTg0LWEwYzctMTkzZDc4NGUzM2E1IiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9wYXJraW5nIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjI3ODk3OWYwLWNhYjMtNGUxYi1hOTNjLWFmYTAwOGNlMTIzNCIsInR5cCI6IkJlYXJlciIsImF6cCI6InBhcmtpbmctY2xpZW50Iiwic2lkIjoiZmRiOTMxMWQtNDEwMy00MmY0LWE5ZmQtNGNlMTY0MmQzNWVjIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJQQVJUTkVSIiwib2ZmbGluZV9hY2Nlc3MiLCJkZWZhdWx0LXJvbGVzLXBhcmtpbmciLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImFjY291bnQiOnsicm9sZXMiOlsibWFuYWdlLWFjY291bnQiLCJtYW5hZ2UtYWNjb3VudC1saW5rcyIsInZpZXctcHJvZmlsZSJdfX0sInNjb3BlIjoicHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjp0cnVlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJsZW9AZ21haWwuY29tIiwiZW1haWwiOiJsZW9AZ21haWwuY29tIn0.FCViiHvW0PiOds35T6Kak2GEoRQVRlBygrvm3x5zy_WzTB35eKnjJy09OWYeVmtA1v27PBR9vQlLB5R7fqDaTUscLxaBbP245baeKCX6UiWMsi-P70CrcuKMEf8URBhHsM1SyzVOsUG87uZ2HAmQrpvQy8N_m3eix3Jp6_v9uFwGiAKyhJ3LXcAZqucwnMxyKY1kcNkiQIH6-OJZKowH0_O0BcgArO0fOJMnrSUCsVOv8WyxYYStLMbUaiCYcLspCDBnTP4mOybfNxy1fCRR5jXBXwa_qIqonim1PXNxpBH1sm0DMJhBWb7J3thJhcdhiTqLcYFmsKOHKXzPtjQjjg";
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "test16", 1200, 80);

        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "278979f0-cab3-4e1b-a93c-afa008ce1234", "test18", 1200, 80);

        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD123");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD124");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141041", this.parkingId(), 2);
        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141042", this.parkingId(), 12);

        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(2),
                this.vehicleId("ABD123"), "2024-07-20 15:31:11.141041");
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(12),
                this.vehicleId("ABD124"), "2024-07-20 15:31:11.141042");

        final PaginationRequestDto paginationRequestDto = new PaginationRequestDto();
        paginationRequestDto.setPage(1);
        paginationRequestDto.setPageSize(1);

        final VehiclesRequestDto vehiclesRequestDto = new VehiclesRequestDto();
        vehiclesRequestDto.setParkingId(this.parkingId());
        vehiclesRequestDto.setPaginationRequest(paginationRequestDto);

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("This member is not authorized to access this parking lot.");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + token)
                .content(OBJECT_MAPPER.writeValueAsBytes(vehiclesRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isForbidden());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_getVehiclesInParking_withVehiclesEmptyAndRoleAdmin_shouldReturnEmptyList() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "test16", 1200, 80);
        final PaginationRequestDto paginationRequestDto = new PaginationRequestDto();
        paginationRequestDto.setPage(1);
        paginationRequestDto.setPageSize(1);

        final VehiclesRequestDto vehiclesRequestDto = new VehiclesRequestDto();
        vehiclesRequestDto.setParkingId(this.parkingId());
        vehiclesRequestDto.setPaginationRequest(paginationRequestDto);

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("The parking does not have vehicles");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(vehiclesRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isNotFound());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
