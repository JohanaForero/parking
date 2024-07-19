package com.forero.parking.controller;

import com.forero.parking.BaseIT;
import com.forero.parking.openapi.model.LoginRequestDto;
import com.forero.parking.openapi.model.RoleDto;
import com.forero.parking.openapi.model.TokenAuthenticationDto;
import com.forero.parking.openapi.model.UserDto;
import com.forero.parking.openapi.model.UserResponseDto;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.net.URI;

class LogInIntegrationTest extends BaseIT {
    private static final String BASE_PATH = "/parking/login";

    void test_logIn_withDynamicRequestValidAndDifferentRole_shouldReturnStatusOkAndUserResponseDto() throws Exception {
        //Given
        final String userName = "edwin";
        final String bearer = "Bearer";
        final int expireIn = 3600;
        final String refreshToken = "sdfasdfqwewe.tytrqertag.qrtq3tgh";
        final String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6ImVkd2luIiwiY3Vzd" +
                "G9tOnJvbGUiOiJBRE1JTklTVFJBVE9SIiwiaWF0IjoxNzA4NzI2MTA2LCJleHAiOjE3MDg3Mjk3MDZ9.7O53n-fOWDzK4qt-txF5XyL" +
                "Yt5Q_IgZA8ucC0kafWRI";

        final LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setUsername(userName);
        loginRequestDto.setPassword("Parking_12345");
        final UserDto userDto = new UserDto();
        userDto.setName(userName);
        userDto.setRole(RoleDto.ADMINISTRATOR);

        final TokenAuthenticationDto tokenAuthenticationDto = new TokenAuthenticationDto();
        tokenAuthenticationDto.setIdToken(token);
        tokenAuthenticationDto.setRefreshToken(refreshToken);
        tokenAuthenticationDto.setExpireIn(expireIn);
        tokenAuthenticationDto.setTokenType(bearer);

        final UserResponseDto expected = new UserResponseDto();
        expected.setUser(userDto);
        expected.setTokenAuthentication(tokenAuthenticationDto);


        //When
        final ResultActions response = this.mockMvc.perform(MockMvcRequestBuilders.post(URI.create(BASE_PATH))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(OBJECT_MAPPER.writeValueAsBytes(loginRequestDto)));

        //Then
        response.andExpect(MockMvcResultMatchers.status().isOk());
        final String body = response.andReturn().getResponse().getContentAsString();
        final UserResponseDto actual = OBJECT_MAPPER.readValue(body, UserResponseDto.class);
        Assertions.assertEquals(expected, actual);
    }
}
