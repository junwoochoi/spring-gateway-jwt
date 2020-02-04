package com.alpha.auth.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
}
