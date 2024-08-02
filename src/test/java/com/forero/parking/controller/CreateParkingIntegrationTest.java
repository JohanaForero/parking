package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.ParkingRequestDto;
import com.forero.parking.openapi.model.ParkingResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;

class CreateParkingIntegrationTest extends BaseIT {

    private static final String BASE_PATH = "/parking/";

    private int parkingId() {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking WHERE name = ?",
                Integer.class, "parking_1");
        return id != null ? id : 0;
    }

    @Test
    void test_createParking_withRequestValidAndCorrect_shouldStatusOkAndParkingResponseDto() throws Exception {
        //Given
        final ParkingRequestDto parkingRequestDto = new ParkingRequestDto();
        parkingRequestDto.setParkingName("parking_1");
        parkingRequestDto.setPartnerId("6899454");
        parkingRequestDto.setNumberOfParkingLots(500);
        parkingRequestDto.setCostPerHour(150);

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingRequestDto)));

        final ParkingResponseDto expected = new ParkingResponseDto();
        expected.setParkingId(this.parkingId());

        //Then
        response.andExpect(MockMvcResultMatchers.status().isCreated());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ParkingResponseDto actual = OBJECT_MAPPER.readValue(body, ParkingResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_createParking_withRequestValidAndRolePartner_shouldStatusForbidden() throws Exception {
        //Given
        final ParkingRequestDto parkingRequestDto = new ParkingRequestDto();
        parkingRequestDto.setParkingName("parking_2");
        parkingRequestDto.setPartnerId("6899455");
        parkingRequestDto.setNumberOfParkingLots(100);
        parkingRequestDto.setCostPerHour(120);

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + PARTNER_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}
