package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.ErrorObjectDto;
import com.forero.parking.openapi.model.ParkingDepartureRequestDto;
import com.forero.parking.openapi.model.ParkingDepartureResponseDto;
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
import java.util.Random;
import java.util.stream.Stream;

class DepartureIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/parking/departure";

    private static Stream<Arguments> provideInvalidParameters() {
        return Stream.of(
                Arguments.of("Without the licence plate", new ParkingDepartureRequestDto(null, 1L), "licensePlate"),
                Arguments.of("Without the parking lot id", new ParkingDepartureRequestDto("ABC123", null),
                        "parkingLotId"),
                Arguments.of("Wit special character on licence plate", new ParkingDepartureRequestDto("ABC-123", 1L),
                        "licensePlate"),
                Arguments.of("Wit more than 6 character on licence plate", new ParkingDepartureRequestDto("ABC123123",
                        1L), "licensePlate"),
                Arguments.of("Wit ñ on licence plate", new ParkingDepartureRequestDto("ABñ123", 1L), "licensePlate"),
                Arguments.of("Wit Ñ on licence plate", new ParkingDepartureRequestDto("ABÑ123", 1L), "licensePlate")
        );
    }

    @Test
    void test_RegisterVehicleExit_withoutBody_shouldThrowBadRequest() throws Exception {
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
    void test_RegisterVehicleExit_withVehicleNotInParkingLot_shouldThrowBadRequest() throws Exception {
        //Given
        final String licensePlate = "123ABc";
        final long parkingLotId = 1;

        final ParkingDepartureRequestDto parkingDepartureRequestDto = new ParkingDepartureRequestDto();
        parkingDepartureRequestDto.setLicensePlate(licensePlate);
        parkingDepartureRequestDto.setParkingLotId(parkingLotId);

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message(String.format("Vehicle with license plate %s is not in parking lot %d",
                licensePlate, parkingLotId));

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingDepartureRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideInvalidParameters")
    void test_RegisterVehicleExit_withoutLicensePlate_shouldThrowBadRequest(final String testName,
                                                                            final ParkingDepartureRequestDto parkingDepartureRequestDto,
                                                                            final String field) throws Exception {
        //Given
        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message(String.format("Invalid %s parameters", field));

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingDepartureRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_RegisterVehicleExit_withValidData_shouldReturnParkingDepartureResponseDto() throws Exception {
        //Given
        final String licensePlate = "nB99";
        final long occupiedParkingLotId = 1;
        this.jdbcTemplate.update("INSERT INTO vehicle (id, license_plate) VALUES (?, ?)", 1, licensePlate);
        this.jdbcTemplate.update("INSERT INTO parking_lot (id, vehicle_id, entrance_date) VALUES (?, ?, ?)", occupiedParkingLotId, 1,
                "2024-07-20 15:31:11.141046");
        this.jdbcTemplate.update("INSERT INTO history (id, parking_lot_id, vehicle_id, entrance_date) VALUES (?, ?, " +
                "?, ?)", 1, occupiedParkingLotId, 1, "2024-07-20 15:31:11.141046");

        final ParkingDepartureRequestDto parkingDepartureRequestDto = new ParkingDepartureRequestDto();
        parkingDepartureRequestDto.setLicensePlate(licensePlate);
        parkingDepartureRequestDto.setParkingLotId(occupiedParkingLotId);

        final ParkingDepartureResponseDto expected = new ParkingDepartureResponseDto();
        expected.setTotalPaid("30.0");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingDepartureRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ParkingDepartureResponseDto actual = OBJECT_MAPPER.readValue(body, ParkingDepartureResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    private long generateRandomParkingLotWithExclusion(final long parkingLotExcluded) {
        final Random random = new Random();
        int parkingLotIdGenerated;
        do {
            parkingLotIdGenerated = random.nextInt(3) + 1;
        } while (parkingLotIdGenerated == parkingLotExcluded);

        return parkingLotIdGenerated;
    }
}
