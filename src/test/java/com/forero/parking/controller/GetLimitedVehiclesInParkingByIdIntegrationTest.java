package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.VehicleParkingDto;
import com.forero.parking.openapi.model.VehicleParkingResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

class GetLimitedVehiclesInParkingByIdIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/parking/vehicles/";

    private int parkingId(final String name) {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking WHERE name = ?",
                Integer.class, name);
        return id != null ? id : 0;
    }

    private int parkingLotId(final int code) {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM parking_lot WHERE code = ?",
                Integer.class, code);
        return id != null ? id : 0;
    }
//
//    private int vehicleId(final String licensePlate) {
//        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM vehicle WHERE license_plate = ?",
//                Integer.class, licensePlate);
//        return id != null ? id : 0;
//    }

    private long vehicleId(final String licensePlate) {
        final Integer id = this.jdbcTemplate.queryForObject("SELECT id FROM vehicle WHERE license_plate = ?",
                Integer.class, licensePlate);
        return id != null ? id : 0;
    }

    @Test
    void test_getLimitedVehiclesInParkingById_withValidDataAndRoleAdmin_shouldReturnVehicleParkingResponseDto() throws Exception {
        //Given
        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
                " VALUES (?, ?, ?, ?)", "c3198aba-e591-45a4-b751-768570ad8fd0", "test16", 1200, 80);

        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD123");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD124");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD125");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD126");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD127");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD128");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD129");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD224");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD324");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD424");
        this.jdbcTemplate.update("INSERT INTO vehicle (license_plate) VALUES (?)", "ABD524");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141041", this.parkingId("test16"), 2);
        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141042", this.parkingId("test16"), 12);

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.141032", this.parkingId("test16"), 9);

        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(9), this.vehicleId("ABD124"),
                "2024-07-20 15:31:11.141032");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.121032", this.parkingId("test16"), 10);
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(10), this.vehicleId("ABD124"),
                "2024-07-20 15:31:11.121032");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.111032", this.parkingId("test16"), 1);
        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.111032", this.parkingId("test16"), 3);

        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(1), this.vehicleId("ABD125"),
                "2024-07-20 15:31:11.121032");
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(3), this.vehicleId("ABD125"),
                "2024-07-20 15:31:11.121032");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.111056", this.parkingId("test16"), 4);

        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(4), this.vehicleId("ABD126"),
                "2024-07-20 15:31:11.141056");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.111059", this.parkingId("test16"), 7);

        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(7), this.vehicleId("ABD127"),
                "2024-07-20 15:31:11.141059");
        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.211059", this.parkingId("test16"), 8);

        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(8), this.vehicleId("ABD128"),
                "2024-07-20 15:31:11.241059");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.212059", this.parkingId("test16"), 27);

        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(27), this.vehicleId("ABD129"),
                "2024-07-20 15:31:11.242059");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.212059", this.parkingId("test16"), 28);

        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(28), this.vehicleId("ABD224"),
                "2024-07-20 15:31:11.242059");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.212049", this.parkingId("test16"), 29);
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(29), this.vehicleId("ABD324"),
                "2024-07-20 15:31:11.242049");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.242019", this.parkingId("test16"), 30);
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(30), this.vehicleId("ABD424"),
                "2024-07-20 15:31:11.242019");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 15:31:11.342019", this.parkingId("test16"), 31);
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(31), this.vehicleId("ABD424"),
                "2024-07-20 15:31:11.342019");

        this.jdbcTemplate.update("INSERT INTO parking_lot (entrance_date, parking_id, code) VALUES (?, ?, ?)",
                "2024-07-20 13:31:11.342019", this.parkingId("test16"), 32);
        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(32), this.vehicleId("ABD524"),
                "2024-07-20 13:31:11.342019");

        this.jdbcTemplate.update("INSERT INTO history (parking_lot_id, vehicle_id, entrance_date) " +
                        "VALUES (?,?,?)", this.parkingLotId(12),
                this.vehicleId("ABD123"), "2024-07-20 15:31:11.141042");

        final VehicleParkingDto vehicle1 = new VehicleParkingDto();
        vehicle1.setId(this.vehicleId("ABD125"));
        vehicle1.setLicensePlate("ABD125");
        vehicle1.setTotal(2);

        final VehicleParkingDto vehicle2 = new VehicleParkingDto();
        vehicle2.setId(this.vehicleId("ABD124"));
        vehicle2.setLicensePlate("ABD124");
        vehicle2.setTotal(2);

        final VehicleParkingDto vehicle3 = new VehicleParkingDto();
        vehicle3.setId(this.vehicleId("ABD424"));
        vehicle3.setLicensePlate("ABD424");
        vehicle3.setTotal(2);

        final VehicleParkingDto vehicle4 = new VehicleParkingDto();
        vehicle4.setId(this.vehicleId("ABD524"));
        vehicle4.setLicensePlate("ABD524");
        vehicle4.setTotal(1);

        final VehicleParkingDto vehicle5 = new VehicleParkingDto();
        vehicle5.setId(this.vehicleId("ABD123"));
        vehicle5.setLicensePlate("ABD123");
        vehicle5.setTotal(1);

        final VehicleParkingDto vehicle6 = new VehicleParkingDto();
        vehicle6.setId(this.vehicleId("ABD126"));
        vehicle6.setLicensePlate("ABD126");
        vehicle6.setTotal(1);

        final VehicleParkingDto vehicle7 = new VehicleParkingDto();
        vehicle7.setId(this.vehicleId("ABD127"));
        vehicle7.setLicensePlate("ABD127");
        vehicle7.setTotal(1);

        final VehicleParkingDto vehicle8 = new VehicleParkingDto();
        vehicle8.setId(this.vehicleId("ABD128"));
        vehicle8.setLicensePlate("ABD128");
        vehicle8.setTotal(1);

        final VehicleParkingDto vehicle9 = new VehicleParkingDto();
        vehicle9.setId(this.vehicleId("ABD324"));
        vehicle9.setLicensePlate("ABD324");
        vehicle9.setTotal(1);

        final VehicleParkingDto vehicle10 = new VehicleParkingDto();
        vehicle10.setId(this.vehicleId("ABD224"));
        vehicle10.setLicensePlate("ABD224");
        vehicle10.setTotal(1);

        final List<VehicleParkingDto> vehicleStatistics = new ArrayList<>();
        vehicleStatistics.add(vehicle1);
        vehicleStatistics.add(vehicle2);
        vehicleStatistics.add(vehicle3);
        vehicleStatistics.add(vehicle4);
        vehicleStatistics.add(vehicle5);
        vehicleStatistics.add(vehicle6);
        vehicleStatistics.add(vehicle7);
        vehicleStatistics.add(vehicle8);
        vehicleStatistics.add(vehicle9);
        vehicleStatistics.add(vehicle10);
        final VehicleParkingResponseDto expected = new VehicleParkingResponseDto();
        expected.setVehicleParking(vehicleStatistics);

        //When
        final ResultActions response =
                this.mockMvc.perform(MockMvcRequestBuilders.get(URI.create(BASE_PATH + this.parkingId("test16")))
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header(AUTHORIZATION, BEARER + ADMIN_TOKEN));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        final String body = response.andReturn().getResponse().getContentAsString();
        final VehicleParkingResponseDto actual = OBJECT_MAPPER.readValue(body, VehicleParkingResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
