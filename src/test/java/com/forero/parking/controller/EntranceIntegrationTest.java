package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.ParkingEntranceRequestDto;
import com.forero.parking.openapi.model.ParkingEntranceResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;

class EntranceIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/parking/entrance";

    private static final String AUTHORIZATION = "Authorization";

    private static final String BEARER = "Bearer ";

    private static final String TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJJRnVxbE9SYWxYbXBpcnFsX251" +
            "eTg1OWwzazZPbWVTVWZST3kwNWdHandzIn0.eyJleHAiOjE3MjE0NTY3MjQsImlhdCI6MTcyMTQzNTEyNCwianRpIjoiMTliOTM2NDYt" +
            "MzQwNy00OWRmLTg2OTQtZmRkZmVkYmFjNWMxIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwL3JlYWxtcy9wYXJraW5nIiwiYXVk" +
            "IjoiYWNjb3VudCIsInN1YiI6IjIyZjU5MGNlLTAxMzEtNDdiZS05NWI3LTM4MWJlZjQyYmJlMiIsInR5cCI6IkJlYXJlciIsImF6cCI6" +
            "InBhcmtpbmctY2xpZW50Iiwic2lkIjoiNmZiOTk3MzEtYjZkNy00MGY0LWEzZmItM2Q4MDFjN2I0ODliIiwiYWNyIjoiMSIsInJlYWxt" +
            "X2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsImRlZmF1bHQtcm9sZXMtcGFya2luZyIsInVtYV9hdXRob3JpemF0aW9u" +
            "IiwiQURNSU4iXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFj" +
            "Y291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwi" +
            "cHJlZmVycmVkX3VzZXJuYW1lIjoiYWRtaW4iLCJnaXZlbl9uYW1lIjoiIiwiZmFtaWx5X25hbWUiOiIiLCJlbWFpbCI6ImFkbWluQG1h" +
            "aWwuY29tIn0.w6bD7i61tLLAIgz0c0wO5v4xf5XAJNjfFf4enXizoRpl1qwCjR9TthXbGe-Eb6DMNjEpLbqrTrFgu3JRIRKwHEKan9PR" +
            "Wrd5WzqHY7zB2oSEqep1ZV6M4-9WGvrTgRywYf1UCUCJEw6XwCIPV0ePHjp4UJKOvKM8VlKJIqjwZvYmfNRBdIk4meEbrd1JRgerX3v_" +
            "Qz4OPe5pW6ezx5Rl7xIQ7BxdENrOps96XA6tW2Q1jDTSUWdgjnZvDk6fnxZDmnO3TeOmLp5Tos7rjmiV0ARKpnqTLcCQ4qaVjgCT8_q0" +
            "02Gn3vVw-ufgu6eTLM7g3wBmcRAObgqAeHFMSMUNJw";

    @Test
    void test_RegisterVehicleEntry_withTokenValid_shouldReturnStatusOkAndParkingEntranceResponseDto() throws Exception {
        //Given
        final ParkingEntranceRequestDto parkingEntranceRequestDto = new ParkingEntranceRequestDto();
        parkingEntranceRequestDto.setLicencePlate("ABC-123");
        parkingEntranceRequestDto.setParkingLotId(1);

        final ParkingEntranceResponseDto expected = new ParkingEntranceResponseDto();
        expected.setId(1);

        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, BEARER + TOKEN)
                .content(OBJECT_MAPPER.writeValueAsBytes(parkingEntranceRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isCreated());
        final String body = response.andReturn().getResponse().getContentAsString();
        final ParkingEntranceResponseDto actual = OBJECT_MAPPER.readValue(body, ParkingEntranceResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
