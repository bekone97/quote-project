package com.example.quoteservice.service;

import com.example.quoteservice.dto.UserDto;
import com.example.quoteservice.dto.UserDtoRequest;
import com.example.quoteservice.dto.UserDtoResponse;
import com.example.quoteservice.security.user.AuthenticatedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDtoResponse getById(String username);

    UserDtoResponse update(String username, UserDtoRequest userDtoRequest, AuthenticatedUser authenticatedUser);

    UserDtoResponse save(String username, UserDtoRequest userDtoRequest, String password);

    void deleteById(String username, AuthenticatedUser authenticatedUser);

    Page<UserDtoResponse> findAll(Pageable pageable);

    void existsByUsername(String username);

    void updatePassword(String username, String password, AuthenticatedUser authenticatedUser);

    UserDto getInnerUserByUsername(String username);
}
