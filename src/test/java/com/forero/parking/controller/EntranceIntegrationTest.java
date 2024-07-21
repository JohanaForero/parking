package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.ErrorObjectDto;
import com.forero.parking.openapi.model.ParkingEntranceRequestDto;
import com.forero.parking.openapi.model.ParkingEntranceResponseDto;
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

class EntranceIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/parking/entrance";

    private static Stream<Arguments> provideInvalidParameters() {
        return Stream.of(
                Arguments.of("Without the licence plate", new ParkingEntranceRequestDto(null, 1L), "licensePlate"),
                Arguments.of("Without the parking lot id", new ParkingEntranceRequestDto("ABC123", null),
                        "parkingLotId"),
                Arguments.of("Wit special character on licence plate", new ParkingEntranceRequestDto("ABC-123", 1L),
                        "licensePlate"),
                Arguments.of("Wit more than 6 character on licence plate", new ParkingEntranceRequestDto("ABC123123",
                        1L), "licensePlate"),
                Arguments.of("Wit ñ on licence plate", new ParkingEntranceRequestDto("ABñ123", 1L), "licensePlate"),
                Arguments.of("Wit Ñ on licence plate", new ParkingEntranceRequestDto("ABÑ123", 1L), "licensePlate")
        );
    }

    public static Stream<Arguments> provideParkingLotNotExists() {
        return Stream.of(
                Arguments.of("Without quantity greater than the existing", 100),
                Arguments.of("Without id 0", 0)
        );
    }

    @Test
    void test_RegisterVehicleEntry_withoutBody_shouldThrowBadRequest() throws Exception {
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideParkingLotNotExists")
    void test_RegisterVehicleEntry_withParkingLotNotExists_shouldThrowBadRequest(final String testName,
                                                                                 final long parkingLotId) throws Exception {
        //Given
        final ParkingEntranceRequestDto parkingEntranceRequestDto = new ParkingEntranceRequestDto();
        parkingEntranceRequestDto.setLicensePlate("ABc123");
        parkingEntranceRequestDto.setParkingLotId(parkingLotId);

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message(String.format("Parking lot %d not found", parkingLotId));

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingEntranceRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_RegisterVehicleEntry_withParkingLotNotFree_shouldThrowBadRequest() throws Exception {
        //Given
        final long occupiedParkingLotId = 1;
        this.jdbcTemplate.update("INSERT INTO vehicle (id, license_plate) VALUES (?, ?)", 1, "ABc123");
        this.jdbcTemplate.update("INSERT INTO parking_lot (id, vehicle_id, entrance_date) VALUES (?, ?, ?)", occupiedParkingLotId, 1,
                "2024-07-20 15:31:11.141046");
        this.jdbcTemplate.update("INSERT INTO history (id, parking_lot_id, vehicle_id, entrance_date) VALUES (?, ?, " +
                "?, ?)", 1, occupiedParkingLotId, 1, "2024-07-20 15:31:11.141046");

        final ParkingEntranceRequestDto parkingEntranceRequestDto = new ParkingEntranceRequestDto();
        parkingEntranceRequestDto.setLicensePlate("ABc1");
        parkingEntranceRequestDto.setParkingLotId(occupiedParkingLotId);

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message(String.format("Parking lot %d is not free", occupiedParkingLotId));

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingEntranceRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_RegisterVehicleEntry_withVehicleInside_shouldThrowBadRequest() throws Exception {
        //Given
        final String licensePlate = "123ABc";
        final long occupiedParkingLotId = 1;
        this.jdbcTemplate.update("INSERT INTO vehicle (id, license_plate) VALUES (?, ?)", 1, licensePlate);
        this.jdbcTemplate.update("INSERT INTO parking_lot (id, vehicle_id, entrance_date) VALUES (?, ?, ?)", occupiedParkingLotId, 1,
                "2024-07-20 15:31:11.141046");
        this.jdbcTemplate.update("INSERT INTO history (id, parking_lot_id, vehicle_id, entrance_date) VALUES (?, ?, " +
                "?, ?)", 1, occupiedParkingLotId, 1, "2024-07-20 15:31:11.141046");

        final long parkingLotId = this.generateRandomParkingLotWithExclusion(occupiedParkingLotId);

        final ParkingEntranceRequestDto parkingEntranceRequestDto = new ParkingEntranceRequestDto();
        parkingEntranceRequestDto.setLicensePlate(licensePlate);
        parkingEntranceRequestDto.setParkingLotId(parkingLotId);

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message(String.format("Vehicle with license plate %s is already inside in parking lot %d",
                licensePlate, occupiedParkingLotId));

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingEntranceRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideInvalidParameters")
    void test_RegisterVehicleEntry_withInvalidParameters_shouldThrowBadRequest(final String testName,
                                                                               final ParkingEntranceRequestDto parkingEntranceRequestDto,
                                                                               final String field) throws Exception {
        //Given
        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message(String.format("Invalid %s parameters", field));

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingEntranceRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_RegisterVehicleEntry_withValidData_shouldReturnParkingEntranceResponseDto() throws Exception {
        //Given
        final ParkingEntranceRequestDto parkingEntranceRequestDto = new ParkingEntranceRequestDto();
        parkingEntranceRequestDto.setLicensePlate("ABc123");
        parkingEntranceRequestDto.setParkingLotId(2L);

        final long idOfLastHistory = this.getIdOfLastHistory();

        final ParkingEntranceResponseDto expected = new ParkingEntranceResponseDto();
        expected.setId(idOfLastHistory + 1);

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingEntranceRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isCreated());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ParkingEntranceResponseDto actual = OBJECT_MAPPER.readValue(body, ParkingEntranceResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    private long getIdOfLastHistory() {
        final Long id = this.jdbcTemplate.queryForObject("SELECT max(id) FROM history", Long.class);
        return id != null ? id : 0;
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
