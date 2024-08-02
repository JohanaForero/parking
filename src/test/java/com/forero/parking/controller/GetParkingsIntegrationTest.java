package com.forero.parking.controller;

import com.forero.parking.BaseIT;

class GetParkingsIntegrationTest extends BaseIT {

    private static final String BASE_PATH = "/parking/centralParking/parkings/";

//    @Test
//    void getBatches_withRequestWithFiltersApplied_shouldReturnFilteredCompanyBatchesPaginatedAndOrderedAscByDeadLine() throws Exception {
//        //Given
//        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
//                " VALUES (?, ?, ?, ?)", "093456734", "tes1", 1200, 80);
//        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
//                " VALUES (?, ?, ?, ?)", "093456735", "tes2", 1200, 80);
//        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
//                " VALUES (?, ?, ?, ?)", "093456736", "tes3", 1200, 80);
//        this.jdbcTemplate.update("INSERT INTO parking (partner_id, name, Cost_Per_Hour, Number_Of_Parking_Lots)" +
//                " VALUES (?, ?, ?, ?)", "093456737", "tes4", 1200, 80);
//
//        final ParkingResponseDto expected = new ParkingResponseDto();
//
//        //When
//
//        // Then
//        response.andExpect(MockMvcResultMatchers.status().isOk());
//        final String body = response.andReturn().getResponse().getContentAsString();
//        final BatchesResponseDto actual = OBJECT_MAPPER.readValue(body, BatchesResponseDto.class);
//        Assertions.assertEquals(expected, actual);
//    }

}
