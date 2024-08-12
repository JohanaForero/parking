package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.ErrorObjectDto;
import com.forero.parking.openapi.model.ParkingRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class GetParkingByIdIntegrationTest extends BaseIT {

    private static final String BASE_PATH = "/parking/centralParking/parking/";

    private int parkingId() {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking WHERE name = ?",
                Integer.class, "tes1");
        return id != null ? id : 0;
    }

    @Test
    void getParkingById_withParkingIdValidRoleAdmin_shouldReturnParkingRequestDtoAndStatusOK() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes1", 1200, 80);
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes2", 1200, 80);

        final ParkingRequestDto expected = new ParkingRequestDto();
        expected.setPartnerId("c3198aba-e591-45a4-b751-768570ad8fd0");
        expected.setParkingName("tes1");
        expected.setCostPerHour(1200);
        expected.setNumberOfParkingLots(80);

        final int idParking = this.parkingId();

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + idParking)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ParkingRequestDto actual = OBJECT_MAPPER.readValue(body, ParkingRequestDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getParkingById_withParkingThatDoesNotExist_shouldReturnParkingNoFoundException() throws Exception {
        //Given

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("Parking not found: " + 9);

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + 9)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getParkingById_withParkingIdValidRolePartner_shouldReturnParkingRequestDtoAndStatusOK() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes13", 1200, 80);
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes2", 1200, 80);
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "093456736", "tes3", 1200, 80);
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes1", 1200, 80);

        final ParkingRequestDto expected = new ParkingRequestDto();
        expected.setPartnerId("c3198aba-e591-45a4-b751-768570ad8fd0");
        expected.setParkingName("tes1");
        expected.setCostPerHour(1200);
        expected.setNumberOfParkingLots(80);

        final int idParking = this.parkingId();

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + idParking)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + PARTNER_TOKEN));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ParkingRequestDto actual = OBJECT_MAPPER.readValue(body, ParkingRequestDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getParkingById_withParkingIdValidRolePartnerToParkingNoAuthorized_shouldReturnUserNoAuthorized() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751", "tes1", 1200, 80);

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("This member is not authorized to access this parking lot.");

        final int idParking = this.parkingId();

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + idParking)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + PARTNER_TOKEN));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isForbidden());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
