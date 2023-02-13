package com.example.quoteservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;

    private String token;

    private LocalDateTime expires;

    private LocalDateTime created;

    private LocalDateTime revoked;

    private String replacedByToken;

    private boolean isActive;

    @ManyToOne
    @JoinColumn(name = "user_username")
    private User user;
}
