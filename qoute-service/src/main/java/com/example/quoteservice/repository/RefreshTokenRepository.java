package com.example.quoteservice.repository;

import com.example.quoteservice.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {

    List<RefreshToken> findRefreshTokensByUserUsername(String username);

    Optional<RefreshToken> findRefreshTokensByToken(String token);
}
