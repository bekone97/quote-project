package com.example.quoteservice.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.quoteservice.dto.UserDtoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Date;

import static java.lang.System.currentTimeMillis;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

public class TestUtil {

    public static final String SUBJECT = "miachyn";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String USERS= "/users";
    public static final String USERS_WITH_USERNAME="/users/{username}";


    public static String getJwtToken() {
        return "Bearer " + JWT.create()
                .withSubject(SUBJECT)
                .withExpiresAt(new Date(currentTimeMillis() + 1000 * 60 * 1000))
                .withIssuer("blogproject.example")
                .withClaim("roles", "ROLE_ADMIN")
                .sign(Algorithm.HMAC256("secret".getBytes()));
    }
    public static ResultActions getUsers(MockMvc mockMvc) throws Exception {
        return mockMvc.perform(get(USERS)
                .param("sort", "username,asc")
                .param("page", "0")
                .param("size", "3"));
    }
    public static ResultActions getUserById(MockMvc mockMvc,String username) throws Exception {
        return mockMvc.perform(MockMvcRequestBuilders.get(USERS_WITH_USERNAME, username)
                .contentType(MediaType.APPLICATION_JSON_VALUE));
    }
    public static ResultActions saveUser(MockMvc mockMvc, String username, String password, String userDtoRequest) throws Exception {
        return mockMvc.perform(post(USERS_WITH_USERNAME, username)
                .param("password", password)
                .contentType(APPLICATION_JSON_VALUE)
                .content(userDtoRequest));
    }

    public static ResultActions updateUser(MockMvc mockMvc,String username,String token,String userDtoRequest) throws Exception {
        return mockMvc.perform(put(USERS_WITH_USERNAME, username)
                .contentType(APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, token)
                .content(userDtoRequest));
    }
    public static ResultActions updateUser(MockMvc mockMvc,String username,String userDtoRequest) throws Exception {
        return mockMvc.perform(put(USERS_WITH_USERNAME, username)
                .contentType(APPLICATION_JSON_VALUE)
                .content(userDtoRequest));
    }
    public static ResultActions deleteUser(MockMvc mockMvc,String username,String token) throws Exception {
        return mockMvc.perform(delete(USERS_WITH_USERNAME, username)
                .contentType(APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, token));
    }
}


