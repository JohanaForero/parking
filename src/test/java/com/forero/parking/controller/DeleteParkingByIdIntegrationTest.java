package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.ErrorObjectDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class DeleteParkingByIdIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/parking/centralParking/parking/";

    private int parkingId() {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking WHERE name = ?",
                Integer.class, "tes1");
        return id != null ? id : 0;
    }

    private int vehicleId(final String licensePlate) {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM vehicle WHERE license_plate = ?",
                Integer.class, licensePlate);
        return id != null ? id : 0;
    }

    private int parkingLotId(final int code) {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking_lot WHERE code = ?",
                Integer.class, code);
        return id != null ? id : 0;
    }

    @Test
    void test_deleteParkingById_withParkingIdValidRoleAdmin_shouldReturnStatusOK() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes1", 1200, 80);
        final int idParking = this.parkingId();

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + idParking)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void test_deleteParkingById_withValidDataAndRolePartner_shouldReturnAccessDeniedException() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes1", 1200, 80);
        final int idParking = this.parkingId();

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("Access Denied");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + idParking)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + PARTNER_TOKEN));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isUnauthorized());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_deleteParkingById_withRequestAndParkingIdNotFoundAndRoleAdmin_shouldReturnParkingNoFoundException() throws Exception {
        //Given
        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("Parking not found: 5");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + 5)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_deleteParkingById_withRequestAndParkingWithCarsInsideRoleAdmin_shouldReturnParkingNotEmpty() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes1", 1200, 80);

        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD123");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141041", this.parkingId(), 2);

        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(2),
                this.vehicleId("ABD123"), "2024-07-20 15:31:11.141041");

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("The parking is not empty!");

        final long parkingId = this.parkingId();

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.delete(BASE_PATH + parkingId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
