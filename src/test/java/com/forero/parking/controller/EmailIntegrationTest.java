package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.ErrorObjectDto;
import com.forero.parking.openapi.model.ParkingEmailRequestDto;
import com.forero.parking.openapi.model.ParkingEmailResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.util.stream.Stream;

class EmailIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/parking/email";

    private static Stream<Arguments> provideInvalidParameters() {
        return Stream.of(
                Arguments.of("Without the email",
                        new ParkingEmailRequestDto(null, "ABC123", "Hello test", "parking name test"),
                        "email"),
                Arguments.of("Without the licence plate",
                        new ParkingEmailRequestDto("test@test.com", null, "Hello test", "parking name test"),
                        "licensePlate"),
                Arguments.of("Without the message",
                        new ParkingEmailRequestDto("test@test.com", "ABC123", null, "parking name test"),
                        "message"),
                Arguments.of("Without the parking name",
                        new ParkingEmailRequestDto("test@test.com", "ABC123", "Hello test", null),
                        "parkingName")
        );
    }

    @Test
    void test_sendEmail_withoutBody_shouldThrowBadRequest() throws Exception {
        //Given
        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("Required request body");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_sendEmail_withVehicleNotInside_shouldThrowBadRequest() throws Exception {
        //Given
        final String licensePlate = "123ABc";

        final ParkingEmailRequestDto parkingEmailRequestDto = new ParkingEmailRequestDto();
        parkingEmailRequestDto.setEmail("test@test.com");
        parkingEmailRequestDto.setLicensePlate(licensePlate);
        parkingEmailRequestDto.setMessage("Test message");
        parkingEmailRequestDto.setParkingName("Parking");

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message(String.format("Vehicle with license plate %s is not inside in parking",
                licensePlate));

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingEmailRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideInvalidParameters")
    void test_sendEmail_withInvalidParameters_shouldThrowBadRequest(final String testName,
                                                                    final ParkingEmailRequestDto parkingEmailRequestDto,
                                                                    final String field) throws Exception {
        //Given
        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message(String.format("Invalid %s parameters", field));

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingEmailRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_sendEmail_withFailEmailServer_shouldThrowInternalServerError() throws Exception {
        //Given
        final String licensePlate = "abc123";
        final long occupiedParkingLotId = 1;
        this.jdbcTemplate.update("INSERT INTO vehicle (id, license_plate) VALUES (?, ?)", 1, licensePlate);
        this.jdbcTemplate.update("INSERT INTO parking_lot (id, vehicle_id, entrance_date) VALUES (?, ?, ?)", occupiedParkingLotId, 1,
                "2024-07-20 15:31:11.141046");
        this.jdbcTemplate.update("INSERT INTO history (id, parking_lot_id, vehicle_id, entrance_date) VALUES (?, ?, " +
                "?, ?)", 1, occupiedParkingLotId, 1, "2024-07-20 15:31:11.141046");

        final ParkingEmailRequestDto parkingEmailRequestDto = new ParkingEmailRequestDto();
        parkingEmailRequestDto.setEmail("test2@test.com");
        parkingEmailRequestDto.setLicensePlate(licensePlate);
        parkingEmailRequestDto.setMessage("Test message");
        parkingEmailRequestDto.setParkingName("FAIL");

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("Error sending email");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingEmailRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isInternalServerError());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_sendEmail_withValidData_shouldReturnParkingEmailResponseDto() throws Exception {
        //Given
        final String licensePlate = "aaabbb";
        final long occupiedParkingLotId = 1;
        this.jdbcTemplate.update("INSERT INTO vehicle (id, license_plate) VALUES (?, ?)", 1, licensePlate);
        this.jdbcTemplate.update("INSERT INTO parking_lot (id, vehicle_id, entrance_date) VALUES (?, ?, ?)", occupiedParkingLotId, 1,
                "2024-07-20 15:31:11.141046");
        this.jdbcTemplate.update("INSERT INTO history (id, parking_lot_id, vehicle_id, entrance_date) VALUES (?, ?, " +
                "?, ?)", 1, occupiedParkingLotId, 1, "2024-07-20 15:31:11.141046");

        final ParkingEmailRequestDto parkingEmailRequestDto = new ParkingEmailRequestDto();
        parkingEmailRequestDto.setEmail("test@test.com");
        parkingEmailRequestDto.setLicensePlate(licensePlate);
        parkingEmailRequestDto.setMessage("Test message");
        parkingEmailRequestDto.setParkingName("Parking");

        final ParkingEmailResponseDto expected = new ParkingEmailResponseDto();
        expected.setMessage("Correo Enviado");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingEmailRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ParkingEmailResponseDto actual = OBJECT_MAPPER.readValue(body, ParkingEmailResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
