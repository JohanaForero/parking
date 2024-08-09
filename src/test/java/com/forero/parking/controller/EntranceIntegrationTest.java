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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.util.stream.Stream;

class EntranceIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/parking/entrance";

    public static Stream<Arguments> prohibitedFormatLicense() {
        return Stream.of(
                Arguments.of("Without the licence plate", null),
                Arguments.of("Wit ñ on licence plate", "ABÑ123"),
                Arguments.of("Wit more than 6 character on licence plate", "ABC123123"),
                Arguments.of("Wit special character on licence plate", "ABC123123")
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
    @WithMockUser(username = USERNAME_PARTNER, roles = {ROLE_PARTNER})
    void test_RegisterVehicleEntry_withParkingLotNotExists_shouldThrowBadRequest(final String testName,
                                                                                 final long code) throws Exception {
        //Give
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "test12", 1200, 80);
        final ParkingEntranceRequestDto parkingEntranceRequestDto = new ParkingEntranceRequestDto();
        parkingEntranceRequestDto.setLicensePlate("ABc123");
        parkingEntranceRequestDto.setCode(code);
        parkingEntranceRequestDto.setParkingId(this.parkingId("test12"));

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message(String.format("Parking lot %d not found", parkingEntranceRequestDto.getCode()));

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

//    @Test
//    @WithMockUser(username = USERNAME_PARTNER, roles = {ROLE_PARTNER})
//    void test_RegisterVehicleEntry_withParkingLotNotFree_shouldThrowBadRequest() throws Exception {
//        //Given
//        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
//                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "test7", 1200, 80);
//        final int parkingId = this.parkingId("test7");
//        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?, ?)", 1, "ABc123");
//        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?," +
//                " ?, ?)", "2024-07-20 15:31:11.141046", parkingId, 12);
//
//        final ParkingEntranceRequestDto parkingEntranceRequestDto = new ParkingEntranceRequestDto();
//        parkingEntranceRequestDto.setLicensePlate("ABC341");
//        parkingEntranceRequestDto.setCode(12L);
//        parkingEntranceRequestDto.setParkingId(this.parkingId("test7"));
//
//        final ErrorObjectDto expected = new ErrorObjectDto();
//        expected.message(String.format("Parking lot %d is not free", parkingEntranceRequestDto.getCode()));
//
//        //When
//        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
//                .contentType(MediaType.APPLICATION_JSON_VALUE)
//                .header(AUTHORIZATION, BEARER + PARTNER_TOKEN)
//                .content(OBJECT_MAPPER.writeValueAsBytes(parkingEntranceRequestDto)));
//
//        //Then
//        response.andExpect(MockMvcResultMatchers.status().isBadRequest());
//        final String body = response.andReturn().getResponse().getContentAsString();
//        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
//        Assertions.assertEquals(expected, actual);
//    }

    @Test
    @WithMockUser(username = USERNAME_PARTNER, roles = {ROLE_PARTNER})
    void test_RegisterVehicleEntry_withVehicleInside_shouldThrowBadRequest() throws Exception {
        //Given
        final String licensePlate = "123ABc";
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "test16", 1200, 80);
        final long parkingId = this.parkingId("test16");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", licensePlate);
        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141046", parkingId, 2);
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                "VALUES (?,?,?)", this.parkingLotId(this.parkingId("test16"), this.vehicleId(licensePlate)), "2024-07" +
                "-20 " +
                "15:31:11.141046");

        final ParkingEntranceRequestDto parkingEntranceRequestDto = new ParkingEntranceRequestDto();
        parkingEntranceRequestDto.setLicensePlate(licensePlate);
        parkingEntranceRequestDto.setCode(2L);
        parkingEntranceRequestDto.setParkingId(this.parkingId("test16"));

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message(String.format("Vehicle with license plate %s is already inside in parking %d",
                licensePlate, parkingId));

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

    @ParameterizedTest(name = "{0}")
    @MethodSource("prohibitedFormatLicense")
    @WithMockUser(username = USERNAME_PARTNER, roles = {ROLE_PARTNER})
    void test_RegisterVehicleEntry_withInvalidLicensePlateInvalid_shouldThrowBadRequest(final String testName,
                                                                                        final String licensePlate) throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes3", 1200, 80);
        final ParkingEntranceRequestDto parkingEntranceRequestDto = new ParkingEntranceRequestDto();
        parkingEntranceRequestDto.setLicensePlate(licensePlate);
        parkingEntranceRequestDto.setCode(32L);
        parkingEntranceRequestDto.setParkingId(this.parkingId("tes3"));
        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("Invalid licensePlate parameters");

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

    private int parkingId(final String parkingName) {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking WHERE name = ?",
                Integer.class, parkingName);
        return id != null ? id : 0;
    }

    private int vehicleId(final String licensePlate) {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM vehicle WHERE license_plate = ?",
                Integer.class, licensePlate);
        return id != null ? id : 0;
    }

    private int parkingLotId(final int parkingId, final int code) {
        final Integer id = this.jdbcTemplate.queryForObject(
                "SELECT id FROM parking_lot WHERE code = ? AND parking_id = ?",
                Integer.class, code, parkingId);
        return id != null ? id : 0;
    }

    @Test
    @WithMockUser(username = USERNAME_PARTNER, roles = {ROLE_PARTNER})
    void test_RegisterVehicleEntry_withValidData_shouldReturnParkingEntranceResponseDto() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes2", 1200, 80);
        final ParkingEntranceRequestDto parkingEntranceRequestDto = new ParkingEntranceRequestDto();
        parkingEntranceRequestDto.setLicensePlate("ABC123");
        parkingEntranceRequestDto.setCode(54L);
        parkingEntranceRequestDto.setParkingId(this.parkingId("tes2"));

        final long idOfLastHistory = this.getIdOfLastHistory();

        final ParkingEntranceResponseDto expected = new ParkingEntranceResponseDto();
        expected.setId(idOfLastHistory + 1);

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + PARTNER_TOKEN)
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
}
