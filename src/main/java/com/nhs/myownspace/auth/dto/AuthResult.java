package com.nhs.myownspace.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResult {
    private boolean success;
    private String message;
    private String accessToken;
    private String refreshToken;

    public static AuthResult success(String at, String rt) {
        return new AuthResult(true, "login success", at, rt);
    }

    public static AuthResult fail(String message) {
        return new AuthResult(false, message, null, null);
    }
}