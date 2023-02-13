package com.example.quoteservice.controller;


import com.example.quoteservice.config.SecurityConfig;
import com.example.quoteservice.dto.UserDtoRequest;
import com.example.quoteservice.dto.UserDtoResponse;
import com.example.quoteservice.exception.ResourceNotFoundException;
import com.example.quoteservice.model.Role;
import com.example.quoteservice.model.User;
import com.example.quoteservice.security.service.JWTService;
import com.example.quoteservice.security.user.AuthenticatedUser;
import com.example.quoteservice.service.UserService;
import com.example.quoteservice.utils.TestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.quoteservice.utils.TestUtil.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@Import(value = {SecurityConfig.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    @MockBean
    private JWTService jwtService;

    private UserDtoResponse userDtoResponse;
    private UserDtoRequest userDtoRequest;
    private User user;
    private String username;
    private String somePassword;
    private org.springframework.security.core.userdetails.User userDetails;
    private AuthenticatedUser authenticatedUser;

    @BeforeEach
    public void setUp() {
        username = SUBJECT;
        userDtoResponse = UserDtoResponse.builder()
                .username(username)
                .dateOfBirth(LocalDate.now().minusYears(13))
                .email("Myachinenergo@mail.ru")
                .build();
        userDtoRequest = UserDtoRequest.builder()
                .dateOfBirth(LocalDate.now().minusYears(13))
                .email("Myachinenergo@mail.ru")
                .build();
        somePassword = "somePassword";
        authenticatedUser = new AuthenticatedUser(username, "laskdlkd", "ROLE_ADMIN");
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority((Role.ROLE_ADMIN.name())));
        userDetails = new org.springframework.security.core.userdetails.User(SUBJECT, somePassword, authorities);
    }

    @AfterEach
    public void tearDown() {
        userDtoRequest = null;
        userDtoResponse = null;
        userDetails=null;
        somePassword=null;
        authenticatedUser=null;
    }


    @SneakyThrows
    @Test
    void getAllUsers() {
        List<UserDtoResponse> userList = List.of(userDtoResponse);
        Pageable pageable = PageRequest.of(0, 3, Sort.by("username"));
        Page<UserDtoResponse> expected = new PageImpl<>(userList, pageable, 1);
        when(userService.findAll(pageable)).thenReturn(expected);

        String actual = getUsers(mockMvc)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), actual);
        verify(userService).findAll(pageable);
    }
    
    @Test
    @SneakyThrows
    void deleteByIdFailUsernameConstraintWithStaticMock() {
        String expectedMessage = "{user.validation.username.min}";
        username="as";
        String timestampFormat = "2000-04-05T11:12:13";
        LocalDateTime timestamp = LocalDateTime.parse(timestampFormat);
        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT, token.substring(TOKEN_PREFIX.length()), "ROLE_ADMIN");
        when(userService.loadUserByUsername(SUBJECT))
                .thenReturn(userDetails);
        try (MockedStatic<LocalDateTime> time = Mockito.mockStatic(LocalDateTime.class)) {
            time.when(LocalDateTime::now).thenReturn(timestamp);
            deleteUser(mockMvc,username,token)
                    .andExpect(status().isBadRequest())
                    .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                    .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)))
                    .andExpect(jsonPath("$['timestamp']", is(timestampFormat)))
                    .andExpect(jsonPath("$['message']", is("Validation error")));

            verify(userService, never()).deleteById(username, authenticatedUser);

        }
    }

    @SneakyThrows
    @Test
    void getUserById() {
        UserDtoResponse expected = userDtoResponse;
        when(userService.getById(username)).thenReturn(userDtoResponse);

        String actual = TestUtil.getUserById(mockMvc,username)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), actual);
        verify(userService).getById(username);
    }



    @SneakyThrows
    @Test
    void getUserByIdFailNoUser() {
        String expectedMessage = "User wasn't found by username";
        when(userService.getById(username)).thenThrow(new ResourceNotFoundException(User.class, "username", username));

       TestUtil.getUserById(mockMvc,username)
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(userService).getById(username);
    }

    @SneakyThrows
    @Test
    void getUserByIdFailWrongUserId() {
        String expectedMessage = "{user.validation.username.min}";
        username = "as";

        TestUtil.getUserById(mockMvc,username)
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(userService, never()).getById(username);
    }


    @Test
    @SneakyThrows
    void save() {
        UserDtoResponse expected = userDtoResponse;
        when(userService.save(username,userDtoRequest, somePassword)).thenReturn(userDtoResponse);

        String actual = TestUtil.saveUser(mockMvc, username,somePassword,objectMapper.writeValueAsString(userDtoRequest))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), actual);
        verify(userService).save(username,userDtoRequest, somePassword);
    }



    @Test
    @SneakyThrows
    void saveFailPasswordConstraint() {
        String expectedMessage = "{user.validation.password.min}";
        somePassword = "sa";
        TestUtil.saveUser(mockMvc, username,somePassword,objectMapper.writeValueAsString(userDtoRequest))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));


        verify(userService, never()).save(username,userDtoRequest, somePassword);
    }

    @Test
    @SneakyThrows
    void saveFailRequestBodyConstraint() {
        String expectedMessage = "{user.validation.email}";
        userDtoRequest.setEmail("sa");
        TestUtil.saveUser(mockMvc, username,somePassword,objectMapper.writeValueAsString(userDtoRequest))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(userService, never()).save(username,userDtoRequest, somePassword);
    }

    @Test
    @SneakyThrows
    void update() {
        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT, token.substring(TOKEN_PREFIX.length()), "ROLE_ADMIN");
        UserDtoResponse expected = userDtoResponse;
        when(userService.loadUserByUsername(SUBJECT))
                .thenReturn(userDetails);
        when(userService.update(username, userDtoRequest, authenticatedUser)).thenReturn(expected);

        String actual = TestUtil.updateUser(mockMvc,username,token,objectMapper.writeValueAsString(userDtoRequest))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        assertEquals(objectMapper.writeValueAsString(expected), actual);
        verify(userService).update(username, userDtoRequest, authenticatedUser);
    }



    @Test
    @SneakyThrows
    void updateFailIdConstraint() {
        String expectedMessage = "{user.validation.username.min}";
        username = "sa";
        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT, token.substring(TOKEN_PREFIX.length()), "ROLE_ADMIN");

        when(userService.loadUserByUsername(SUBJECT))
                .thenReturn(userDetails);

        TestUtil.updateUser(mockMvc,username,token,objectMapper.writeValueAsString(userDtoRequest))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(userService, never()).update(username, userDtoRequest, authenticatedUser);
    }

    @Test
    @SneakyThrows
    void updateFailRequestBodyConstraint() {
        String expectedMessage = "{user.validation.email}";
        userDtoRequest.setEmail("sa");
        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT, token.substring(TOKEN_PREFIX.length()), "ROLE_ADMIN");

        when(userService.loadUserByUsername(SUBJECT))
                .thenReturn(userDetails);

        TestUtil.updateUser(mockMvc,username,token,objectMapper.writeValueAsString(userDtoRequest))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(result -> assertTrue(result.getResolvedException().getMessage().contains(expectedMessage)));

        verify(userService, never()).update(username, userDtoRequest, authenticatedUser);
    }


    @Test
    @SneakyThrows
    void updateFailRequestAuthorizationConstraint() {
        TestUtil.updateUser(mockMvc,username,objectMapper.writeValueAsString(userDtoRequest))
                .andExpect(status().isUnauthorized());

        verify(userService, never()).update(username, userDtoRequest, authenticatedUser);
        verify(userService, never()).loadUserByUsername(SUBJECT);
    }

    @Test
    @SneakyThrows
    void deleteById() {

        String token = getJwtToken();
        authenticatedUser = new AuthenticatedUser(SUBJECT, token.substring(TOKEN_PREFIX.length()), "ROLE_ADMIN");
        when(userService.loadUserByUsername(SUBJECT))
                .thenReturn(userDetails);

        TestUtil.deleteUser(mockMvc,username,token)
                .andExpect(status().isOk());

        verify(userService).deleteById(username, authenticatedUser);
    }


}