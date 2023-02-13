package com.example.quoteservice.service.impl;

import com.example.quoteservice.dto.UserDto;
import com.example.quoteservice.exception.ResourceNotFoundException;
import com.example.quoteservice.exception.TokenNotActiveException;
import com.example.quoteservice.mapper.UserMapper;
import com.example.quoteservice.model.RefreshToken;
import com.example.quoteservice.repository.RefreshTokenRepository;
import com.example.quoteservice.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.secret}")
    private String SECRET_KEY;


    @Override
    @Transactional
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public RefreshToken update(RefreshToken refreshToken, Long id) {
        refreshToken.setRefreshTokenId(id);
        return refreshTokenRepository.save(refreshToken);
    }


    @Override
    public void deactivateRefreshTokensByUserId(String username) {
        refreshTokenRepository.findRefreshTokensByUserUsername(username)
                .stream()
                .filter(RefreshToken::isActive)
                .forEach(refreshToken -> {
                    refreshToken.setActive(false);
                    refreshToken.setRevoked(LocalDateTime.now());
                    refreshTokenRepository.save(refreshToken);
                });
    }

    @Transactional
    public void deactivateToken(RefreshToken refreshToken, LocalDateTime currentDate) {
        refreshToken.setActive(false);
        refreshToken.setRevoked(currentDate);
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    @Transactional
    public RefreshToken createRefreshToken(UserDto user) {
        LocalDateTime currentDate = LocalDateTime.now();
        String token = getRefreshToken(user, currentDate);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(Base64.getEncoder().encodeToString(token.getBytes()))
                .user(UserMapper.INSTANCE.convert(user))
                .expires(currentDate.plusDays(7))
                .created(currentDate)
                .isActive(true)
                .build();
        return save(refreshToken);
    }

    @Override
    public RefreshToken getByToken(String token) {
        return refreshTokenRepository.findRefreshTokensByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException(RefreshToken.class, "refreshToken", token));
    }


    @Override
    @Transactional
    public RefreshToken replaceToken(RefreshToken oldToken, UserDto user, LocalDateTime currentDate) {
        checkOldToken(oldToken, user, currentDate);
        deactivateToken(oldToken, currentDate);
        String newToken = getRefreshToken(user, currentDate);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(Base64.getEncoder().encodeToString(newToken.getBytes()))
                .user(UserMapper.INSTANCE.convert(user))
                .expires(currentDate.plusDays(7))
                .created(currentDate)
                .isActive(true)
                .replacedByToken(oldToken.getToken())
                .build();
        return save(refreshToken);
    }


    private String getRefreshToken(UserDto user, LocalDateTime currentDate) {
        return new StringBuffer(user.getUsername())
                .append(user.getRole())
                .append(currentDate)
                .append(currentDate.plusDays(7))
                .append(SECRET_KEY)
                .toString();
    }

    private void checkTokenIsActive(RefreshToken token, String username) {
        if (token.isActive())
            return;
        deactivateRefreshTokensByUserId(username);
        throw new TokenNotActiveException();
    }

    private void checkTokenExpires(RefreshToken token, LocalDateTime currentDate, String username) {
        if (currentDate.isBefore(token.getExpires()))
            return;
        deactivateRefreshTokensByUserId(username);
        throw new TokenNotActiveException();
    }

    private void checkOldToken(RefreshToken token, UserDto user, LocalDateTime currentDate) {
        checkTokenIsActive(token, user.getUsername());
        checkTokenExpires(token, currentDate, user.getUsername());
    }


}
