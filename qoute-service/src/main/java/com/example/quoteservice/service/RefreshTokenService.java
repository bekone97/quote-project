package com.example.quoteservice.service;

import com.example.quoteservice.dto.UserDto;
import com.example.quoteservice.model.RefreshToken;

import java.time.LocalDateTime;


public interface RefreshTokenService {
    void deactivateRefreshTokensByUserId(String username);

    RefreshToken createRefreshToken(UserDto user);

    RefreshToken getByToken(String refreshToken);

    RefreshToken replaceToken(RefreshToken token, UserDto user, LocalDateTime currentDate);

    RefreshToken save(RefreshToken refreshToken);

    RefreshToken update(RefreshToken refreshToken, Long id);
}
