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

    @Test
    void test_RegisterVehicleEntry_withParkingLotNotFree_shouldThrowBadRequest() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "test199", 1200, 80);
        final int parkingId = this.parkingId("test199");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES ( ?)", "ABR123");
        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?," +
                " ?, ?)", "2024-07-20 15:31:11.141046", parkingId, 12);
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                "VALUES (?,?,?)", this.parkingLotId(parkingId), this.vehicleId("ABR123"), "2024-07-20 15:31:11.141046");

        final ParkingEntranceRequestDto parkingEntranceRequestDto = new ParkingEntranceRequestDto();
        parkingEntranceRequestDto.setLicensePlate("ABC341");
        parkingEntranceRequestDto.setCode(12L);
        parkingEntranceRequestDto.setParkingId(this.parkingId("test199"));

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message(String.format("Parking code %d is not free", parkingEntranceRequestDto.getCode()));

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
    void test_RegisterVehicleEntry_withVehicleInside_shouldThrowBadRequest() throws Exception {
        //Given
        final String licensePlate = "123ABc";
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "test16", 1200, 80);
        final int parkingId = this.parkingId("test16");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", licensePlate);
        final int vehicleId = this.vehicleId(licensePlate);
        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141046", parkingId, 2);
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                "VALUES (?,?,?)", this.parkingLotId(parkingId), vehicleId, "2024-07" +
                "-20 " +
                "15:31:11.141046");

        final ParkingEntranceRequestDto parkingEntranceRequestDto = new ParkingEntranceRequestDto();
        parkingEntranceRequestDto.setLicensePlate(licensePlate);
        parkingEntranceRequestDto.setCode(2L);
        parkingEntranceRequestDto.setParkingId(this.parkingId("test16"));

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message(String.format("Vehicle with license plate %s is in parking lot %d", licensePlate, parkingId));

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

    private int parkingLotId(final int parkingId) {
        final Integer id = this.jdbcTemplate.queryForObject(
                "SELECT id FROM parking_lot WHERE parking_id = ?",
                Integer.class, parkingId);
        return id != null ? id : 0;
    }

    @Test
    void test_RegisterVehicleEntry_withValidData_shouldReturnParkingEntranceResponseDto() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes32", 1200, 80);
        final ParkingEntranceRequestDto parkingEntranceRequestDto = new ParkingEntranceRequestDto();
        parkingEntranceRequestDto.setLicensePlate("ABC123");
        parkingEntranceRequestDto.setCode(54L);
        parkingEntranceRequestDto.setParkingId(this.parkingId("tes32"));

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + PARTNER_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingEntranceRequestDto)));

        final long parkingLot = this.parkingLotId(this.parkingId("tes32"));

        final ParkingEntranceResponseDto expected = new ParkingEntranceResponseDto();
        expected.setId(parkingLot);

        //Then
        response.andExpect(MockMvcResultMatchers.status().isCreated());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ParkingEntranceResponseDto actual = OBJECT_MAPPER.readValue(body, ParkingEntranceResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void test_RegisterVehicleEntry_withValidDataToParkingNotAuthorized_shouldReturnUserNotAutorized() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "tes32", 1200, 80);
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8op0", "tes33", 1200, 80);
        final ParkingEntranceRequestDto parkingEntranceRequestDto = new ParkingEntranceRequestDto();
        parkingEntranceRequestDto.setLicensePlate("ABC123");
        parkingEntranceRequestDto.setCode(54L);
        parkingEntranceRequestDto.setParkingId(this.parkingId("tes33"));

        final ErrorObjectDto expected = new ErrorObjectDto();
        expected.message("This member is not authorized to access this parking lot.");


        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + PARTNER_TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingEntranceRequestDto)));

        // Then
        response.andExpect(MockMvcResultMatchers.status().isForbidden());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ErrorObjectDto actual = OBJECT_MAPPER.readValue(body, ErrorObjectDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
