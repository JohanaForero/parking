package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.ErrorObjectDto;
import com.forero.parking.openapi.model.UpdateParkingCompleteRequestDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;

class UpdateParkingIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/parking/centralParking/";

    private int parkingId() {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking WHERE name = ?",
                Integer.class, "tes1");
        return id != null ? id : 0;
    }

    @Test
    void test_updateParking_withRequestValidRoleAdmin_shouldReturnStatusNoContent() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes1", 1200, 80);
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes2", 1200, 80);

        final UpdateParkingCompleteRequestDto updateParkingRequestDto = new UpdateParkingCompleteRequestDto();
        updateParkingRequestDto.setParkingId(this.parkingId());
        updateParkingRequestDto.setCostPerHour(1500);
        updateParkingRequestDto.setParkingName("tes1-Actualizado");
        updateParkingRequestDto.setNumberOfParkingLots(45);
        updateParkingRequestDto.setPartnerId("c3198aba-e591-45a4-b751-768570ad8fui");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(updateParkingRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void test_updateParking_withRequestWithExistingNameRoleAdmin_shouldReturnParkingNameAlreadyExistsException() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes1", 1200, 80);
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes2", 1200, 80);

        final UpdateParkingCompleteRequestDto updateParkingRequestDto = new UpdateParkingCompleteRequestDto();
        updateParkingRequestDto.setParkingId(this.parkingId());
        updateParkingRequestDto.setCostPerHour(1500);
        updateParkingRequestDto.setParkingName("tes2");
        updateParkingRequestDto.setNumberOfParkingLots(45);
        updateParkingRequestDto.setPartnerId("c3198aba-e591-45a4-b751-768570ad8fui");

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("Parking with name tes2 it already exists");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.put(URI.create(BASE_PATH))
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
    void test_updateParking_withParkingIdNotFoundValidRoleAdmin_shouldReturnParkingNoFoundException() throws Exception {
        //Given
        final UpdateParkingCompleteRequestDto updateParkingRequestDto = new UpdateParkingCompleteRequestDto();
        updateParkingRequestDto.setParkingId(53);
        updateParkingRequestDto.setCostPerHour(1500);
        updateParkingRequestDto.setParkingName("tes2");
        updateParkingRequestDto.setNumberOfParkingLots(45);
        updateParkingRequestDto.setPartnerId("c3198aba-e591-45a4-b751-768570ad8fui");

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("Parking not found: 53");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH)
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
    void test_updateParking_withValidDataAndRolePartner_shouldReturnAccessDeniedException() throws Exception {
        //Given
        final UpdateParkingCompleteRequestDto updateParkingRequestDto = new UpdateParkingCompleteRequestDto();
        updateParkingRequestDto.setParkingId(53);
        updateParkingRequestDto.setCostPerHour(1500);
        updateParkingRequestDto.setParkingName("tes2");
        updateParkingRequestDto.setNumberOfParkingLots(45);
        updateParkingRequestDto.setPartnerId("c3198aba-e591-45a4-b751-768570ad8fui");

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("Access Denied");
        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + PARTNER_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(updateParkingRequestDto)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isUnauthorized());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_updateParking_withValidDataAndTheSameNameAndRoleAdmin_shouldReturnStatusNoContent() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes1", 1200, 80);
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes2", 1200, 80);

        final UpdateParkingCompleteRequestDto updateParkingRequestDto = new UpdateParkingCompleteRequestDto();
        updateParkingRequestDto.setParkingId(this.parkingId());
        updateParkingRequestDto.setCostPerHour(1500);
        updateParkingRequestDto.setParkingName("tes1");
        updateParkingRequestDto.setNumberOfParkingLots(45);
        updateParkingRequestDto.setPartnerId("c3198aba-e591-45a4-b751-768570ad8fui");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(updateParkingRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    private int parkingLotId() {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking_lot WHERE code = ?",
                Integer.class, 12);
        return id != null ? id : 0;
    }

    private int vehicleId() {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM vehicle WHERE license_plate = ?",
                Integer.class, "ABD123");
        return id != null ? id : 0;
    }
    
    @Test
    void test_updateParking_withRequestWhenCodeIsLower_shouldReturnParkingCodeConflictException() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes1", 1200, 80);

        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD123");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141041", this.parkingId(), 2);
        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141042", this.parkingId(), 12);

        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(),
                this.vehicleId(), "2024-07-20 15:31:11.141042");

        final UpdateParkingCompleteRequestDto updateParkingRequestDto = new UpdateParkingCompleteRequestDto();
        updateParkingRequestDto.setParkingId(this.parkingId());
        updateParkingRequestDto.setCostPerHour(1500);
        updateParkingRequestDto.setParkingName("tes1");
        updateParkingRequestDto.setNumberOfParkingLots(5);
        updateParkingRequestDto.setPartnerId("c3198aba-e591-45a4-b751-768570ad8fd0");

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("The quota of lots to be updated must not be less than the one currently in use!");
        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.put(BASE_PATH)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(updateParkingRequestDto)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
