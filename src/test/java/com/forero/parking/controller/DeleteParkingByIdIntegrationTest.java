package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class DeleteParkingByIdIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/parking/centralParking/";

    private int parkingId() {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking WHERE name = ?",
                Integer.class, "tes1");
        return id != null ? id : 0;
    }

    @Test
    void test_deleteParkingById_withParkingIdValidRoleAdmin_shouldReturnStatusOK() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes1", 1200, 80);
        final int idParking = this.parkingId();

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.get(BASE_PATH + idParking)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
    }
}
