package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.ErrorObjectDto;
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

    private static final String BASE_PATH = "/parking/centralParking/";

    private int parkingId() {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parkings WHERE name = ?",
                Integer.class, "parking_2");
        return id != null ? id : 0;
    }

    @Test
    void test_createParking_withRequestValidAndCorrect_shouldStatusOkAndParkingResponseDto() throws Exception {
        //Given
        final ParkingRequestDto parkingRequestDto = new ParkingRequestDto();
        parkingRequestDto.setParkingName("parking_2");
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
    void test_createParking_withRequestWithParkingNameDuplicate_shouldExceptionParkingNameAlreadyExistsException()
            throws Exception {
        //Given
        final String existingParkingName = "prueba8";
        this.jdbcTemplate.update("INSERT INTO parkings (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "093456734", existingParkingName, 1200, 80);
        final ParkingRequestDto parkingRequestDto = new ParkingRequestDto();
        parkingRequestDto.setParkingName(existingParkingName);
        parkingRequestDto.setPartnerId("6899455");
        parkingRequestDto.setNumberOfParkingLots(100);
        parkingRequestDto.setCostPerHour(120);

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("Parking with name prueba8 it already exists");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
