package com.forero.parking.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenAuthentication {
    private String idToken;
    private String refreshToken;
    private Integer expireIn;
    private String tokenType;
}
