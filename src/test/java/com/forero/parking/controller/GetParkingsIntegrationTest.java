package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.ParkingDto;
import com.forero.parking.openapi.model.ParkingsResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

class GetParkingsIntegrationTest extends BaseIT {

    private static final String BASE_PATH = "/parking/centralParking/parkings/";

    private int parkingId(final String parkingName) {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parkings WHERE name = ?",
                Integer.class, parkingName);
        return id != null ? id : 0;
    }

    @Test
    void getParkings_withRequestValidAndRolePartner_shouldReturnParkingsResponseDtoAnsStatusOK() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parkings (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "22f590ce-0131-47be-95b7-381bef42bbe2", "tes1", 1200, 80);
        this.jdbcTemplate.update("INSERT INTO parkings (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "22f590ce-0131-47be-95b7-381bef42bbe2", "tes2", 1200, 80);
        this.jdbcTemplate.update("INSERT INTO parkings (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "093456736", "tes3", 1200, 80);
        this.jdbcTemplate.update("INSERT INTO parkings (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "093456737", "tes4", 1200, 80);

        final ParkingDto parking1 = new ParkingDto();
        parking1.setParkingId(this.parkingId("tes1"));
        parking1.setParkingName("tes1");
        final ParkingDto parking2 = new ParkingDto();
        parking1.setParkingId(this.parkingId("tes2"));
        parking2.setParkingName("tes2");
        final List<ParkingDto> parkingDtos = new ArrayList<>();
        parkingDtos.add(parking1);
        parkingDtos.add(parking2);
        final ParkingsResponseDto expected = new ParkingsResponseDto();
        expected.setParking(parkingDtos);

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER, PARTNER_TOKEN));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ParkingsResponseDto actual = OBJECT_MAPPER.readValue(body, ParkingsResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

}
