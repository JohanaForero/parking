package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.ErrorObjectDto;
import com.forero.parking.openapi.model.ParkingDepartureResponseDto;
import com.forero.parking.openapi.model.ParkingEntranceRequestDto;
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

class DepartureIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/parking/departure";

    private static Stream<Arguments> provideInvalidParameters() {
        return Stream.of(
                Arguments.of("Without the licence plate", null, "licensePlate"),
                Arguments.of("Wit special character on licence plate", "12@ASE", "licensePlate"),
                Arguments.of("Wit more than 6 character on licence plate", "ABC123123", "licensePlate"),
                Arguments.of("Wit ñ on licence plate", "ABñ123", "licensePlate"));
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
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "test16", 1200, 80);
        final int parkingId = this.parkingId();
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD123");
        final int vehicleId = this.vehicleId("ABD123");
        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141046", parkingId, 2);
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                "VALUES (?,?,?)", this.parkingLotId(parkingId), vehicleId, "2024-07-20 15:31:11.141046");

        final ParkingEntranceRequestDto parkingDepartureRequestDto = new ParkingEntranceRequestDto();
        parkingDepartureRequestDto.setLicensePlate(licensePlate);
        parkingDepartureRequestDto.setParkingId(this.parkingId());
        parkingDepartureRequestDto.setCode(3L);

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("Vehicle no found: 123ABc");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + PARTNER_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingDepartureRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideInvalidParameters")
    void test_RegisterVehicleExit_withInvalidParameters_shouldThrowBadRequest(final String testName,
                                                                              final String licensePlate,
                                                                              final String field) throws Exception {

        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "test16", 1200, 80);
        final int parkingId = this.parkingId();
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD123");
        final int vehicleId = this.vehicleId("ABD123");
        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141046", parkingId, 2);
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                "VALUES (?,?,?)", this.parkingLotId(parkingId), vehicleId, "2024-07-20 15:31:11.141046");
        final ParkingEntranceRequestDto parkingEntranceRequestDto = new ParkingEntranceRequestDto();
        parkingEntranceRequestDto.setParkingId(parkingId);
        parkingEntranceRequestDto.setLicensePlate(licensePlate);
        parkingEntranceRequestDto.setCode(3L);
        //Given
        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message(String.format("Invalid %s parameters", field));

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + PARTNER_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingEntranceRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_RegisterVehicleExit_withValidData_shouldReturnParkingDepartureResponseDto() throws Exception {
        //Given
        final String licensePlate = "123ABc";
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "test16", 1200, 80);
        final int parkingId = this.parkingId();
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", licensePlate);
        final int vehicleId = this.vehicleId(licensePlate);
        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141046", parkingId, 2);
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                "VALUES (?,?,?)", this.parkingLotId(parkingId), vehicleId, "2024-07" +
                "-20 " +
                "15:31:11.141046");

        final ParkingEntranceRequestDto parkingDepartureRequestDto = new ParkingEntranceRequestDto();
        parkingDepartureRequestDto.setLicensePlate(licensePlate);
        parkingDepartureRequestDto.setCode(2L);
        parkingDepartureRequestDto.setParkingId(parkingId);

        final ParkingDepartureResponseDto expected = new ParkingDepartureResponseDto();
        expected.setTotalPaid("3600.0");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + PARTNER_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingDepartureRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ParkingDepartureResponseDto actual = OBJECT_MAPPER.readValue(body, ParkingDepartureResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    private int parkingId() {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking WHERE name = ?",
                Integer.class, "test16");
        return id != null ? id : 0;
    }

    private int vehicleId(final String licensePlate) {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM vehicle WHERE license_plate = ?",
                Integer.class, licensePlate);
        return id != null ? id : 0;
    }

    private int parkingLotId(final int parkingId) {
        final Integer id = this.jdbcTemplate.queryForObject(
                "SELECT id FROM parking_lot WHERE parking_id = ?",
                Integer.class, parkingId);
        return id != null ? id : 0;
    }

    @Test
    void test_RegisterVehicleExit_withValidDataAndRoleAdmin_shouldReturnAccessDeniedException() throws Exception {
        //Given
        final ParkingEntranceRequestDto parkingDepartureRequestDto = new ParkingEntranceRequestDto();
        parkingDepartureRequestDto.setLicensePlate("123ABc");
        parkingDepartureRequestDto.setCode(2L);
        parkingDepartureRequestDto.setParkingId(2);

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("Access Denied");
        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + ADMIN_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingDepartureRequestDto)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isForbidden());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_RegisterVehicleExit_withParkingIdValidRolePartnerToParkingNoAuthorized_shouldReturnUserNoAuthorized() throws Exception {
        //Given
        final String licensePlate = "123ABc";
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad", "test16", 1200, 80);
        final int parkingId = this.parkingId();
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "test16", 1200, 80);

        final ParkingEntranceRequestDto parkingDepartureRequestDto = new ParkingEntranceRequestDto();
        parkingDepartureRequestDto.setLicensePlate(licensePlate);
        parkingDepartureRequestDto.setCode(2L);
        parkingDepartureRequestDto.setParkingId(parkingId);

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("This member is not authorized to access this parking lot.");

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + PARTNER_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingDepartureRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isForbidden());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
