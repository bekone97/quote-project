package com.example.quoteservice.security.service;

import com.example.quoteservice.dto.UserDto;

import java.util.Map;

public interface JWTService {
    Map<String, String> createAccessAndRefreshTokens(UserDto user);

    String getUsernameByTokenHeader(String header);

    Map<String, String> createTokensByRefreshTokenHeader(String header);
}
