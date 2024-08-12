package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.ErrorObjectDto;
import com.forero.parking.openapi.model.UpdateParkingRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

class UpdateParkingPartiallyIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/parking/centralParking/";

    private int parkingId() {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking WHERE name = ?",
                Integer.class, "tes1");
        return id != null ? id : 0;
    }

    @Test
    void test_updateParkingPartially_withParkingIdValidRoleAdmin_shouldReturnStatusNoContent() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes1", 1200, 80);
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes2", 1200, 80);

        final UpdateParkingRequestDto updateParkingRequestDto = new UpdateParkingRequestDto();
        updateParkingRequestDto.setParkingId(this.parkingId());
        updateParkingRequestDto.setCostPerHour(1500);

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(updateParkingRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void test_updateParkingPartially_withParkingIdNotFoundValidRoleAdmin_shouldReturnParkingNoFoundException() throws Exception {
        //Given
        final UpdateParkingRequestDto updateParkingRequestDto = new UpdateParkingRequestDto();
        updateParkingRequestDto.setParkingId(12);
        updateParkingRequestDto.setCostPerHour(1500);

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("Parking not found: 12");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(updateParkingRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_updateParkingPartially_withValidDataAndRolePartner_shouldReturnAccessDeniedException() throws Exception {
        //Given
        final UpdateParkingRequestDto updateParkingRequestDto = new UpdateParkingRequestDto();
        updateParkingRequestDto.setParkingId(12);
        updateParkingRequestDto.setCostPerHour(1500);

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("Access Denied");
        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.patch(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + PARTNER_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(updateParkingRequestDto)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isUnauthorized());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
