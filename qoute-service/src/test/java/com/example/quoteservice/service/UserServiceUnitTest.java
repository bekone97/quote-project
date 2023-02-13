package com.example.quoteservice.service;

import com.example.quoteservice.dto.QuoteDto;
import com.example.quoteservice.dto.UserDto;
import com.example.quoteservice.dto.UserDtoRequest;
import com.example.quoteservice.dto.UserDtoResponse;
import com.example.quoteservice.exception.NotUniqueResourceException;
import com.example.quoteservice.exception.NotValidCredentialsException;
import com.example.quoteservice.exception.NotValidTokenException;
import com.example.quoteservice.exception.ResourceNotFoundException;
import com.example.quoteservice.mapper.QuoteMapper;
import com.example.quoteservice.mapper.UserMapper;
import com.example.quoteservice.model.Quote;
import com.example.quoteservice.model.Role;
import com.example.quoteservice.model.User;
import com.example.quoteservice.repository.UserRepository;
import com.example.quoteservice.security.user.AuthenticatedUser;
import com.example.quoteservice.service.impl.UserServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({SpringExtension.class})
class UserServiceUnitTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    User user;
    UserDto userDto;
    UserDtoResponse userDtoResponse;
    UserDtoRequest userDtoRequest;
    AuthenticatedUser authenticatedUser;
    String username;
    List<QuoteDto> quotes;
    List<Quote> quote ;
    @BeforeEach
    void setUp() {
        quotes = new ArrayList<>();
        quote=new ArrayList<>();
        username="Myachin";
        user = User.builder()
                .username(username)
                .password("Artsiom")
                .email("myachinenergo@mail.ru")
                .quotes(quote)
                .dateOfBirth(LocalDate.now().minusYears(12))
                .role(Role.ROLE_ADMIN)
                .build();

        userDtoRequest=UserDtoRequest.builder()
                .email("myachinenergo@mail.ru")
                .dateOfBirth(LocalDate.now().minusYears(12))
                .build();

        userDto=UserMapper.INSTANCE.convertToInner(user);
        authenticatedUser = new AuthenticatedUser("Myachin",
                "someToken", Role.ROLE_ADMIN.name());
        userDtoResponse=UserMapper.INSTANCE.convert(user);
    }

    @AfterEach
    void tearDown() {
        username=null;
        userDtoRequest=null;
        userDtoResponse=null;
        userDto=null;
        authenticatedUser=null;
    }

    @Test
    void getById() {
        UserDtoResponse expected = userDtoResponse;
        when(userRepository.findById(username)).thenReturn(Optional.ofNullable(user));


        UserDtoResponse actual = userService.getById(username);

        assertEquals(actual, expected);
        verify(userRepository).findById(username);
    }

    @Test
    void getByIdFail() {
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> userService.getById(username));

        assertTrue(actual.getMessage().contains("User wasn't found by username="+username));
        verify(userRepository).findById(username);
    }

    @Test
    void update() {
        userDtoRequest.setDateOfBirth(LocalDate.now().minusYears(13));
        User changedUser = user;
        user.setDateOfBirth(LocalDate.now().minusYears(13));
        UserDtoResponse expected = userDtoResponse;
        expected.setDateOfBirth(LocalDate.now().minusYears(13));

        when(userRepository.findById(username)).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(changedUser)).thenReturn(changedUser);


        UserDtoResponse actual = userService.update(username, userDtoRequest, authenticatedUser);

        assertEquals(expected, actual);

        verify(userRepository).findById(username);
        verify(userRepository, never()).existsById(username);
        verify(userRepository, never()).existsByEmail(expected.getUsername());
        verify(userRepository).save(changedUser);
    }

    @Test
    void save() {
        String incomingPassword = "artsiom";
        user.setRole(Role.ROLE_USER);
        user.setPassword(incomingPassword);
        UserDtoResponse expected = userDtoResponse;
        when(userRepository.save(user)).thenReturn(user);
        when(userRepository.existsById(username)).thenReturn(false);
        when(userRepository.existsByEmail(userDtoRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(incomingPassword)).thenReturn(incomingPassword);

        UserDtoResponse actual = userService.save(username,userDtoRequest, incomingPassword);

        assertEquals(expected, actual);
        verify(userRepository).save(user);
        verify(passwordEncoder).encode(incomingPassword);
        verify(userRepository).existsById(username);
        verify(userRepository).existsByEmail(userDtoRequest.getEmail());
    }

    @Test
    void saveFailUsername() {
        when(userRepository.existsById(username)).thenReturn(true);

        NotUniqueResourceException actual = assertThrows(NotUniqueResourceException.class,
                () -> userService.save(username,userDtoRequest, "somePassword"));

        assertTrue(actual.getMessage().contains("User already exists with username="+username));
        verify(userRepository, never()).save(user);
        verify(userRepository).existsById(username);
        verify(userRepository,never()).existsByEmail(userDtoRequest.getEmail());
    }
    @Test
    void saveFailEmail() {
        when(userRepository.existsById(username)).thenReturn(false);
        when(userRepository.existsByEmail(userDtoRequest.getEmail())).thenReturn(true);

        NotUniqueResourceException actual = assertThrows(NotUniqueResourceException.class,
                () -> userService.save(username,userDtoRequest, "somePassword"));

        assertTrue(actual.getMessage().contains("User already exists with email="+userDtoRequest.getEmail()));
        verify(userRepository, never()).save(user);
        verify(userRepository).existsById(username);
        verify(userRepository).existsByEmail(userDtoRequest.getEmail());
    }


    @Test
    void deleteById() {
        when(userRepository.findById(username)).thenReturn(Optional.of(user));
        userService.deleteById(username, authenticatedUser);

        verify(userRepository).findById(username);
        verify(userRepository).deleteById(username);

    }
    @Test
    void deleteByIdFailUsername() {
        when(userRepository.findById(username)).thenReturn(Optional.empty());

        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteById(username, authenticatedUser));

        assertTrue(actual.getMessage().contains("User wasn't found by username="+username));
        verify(userRepository).findById(username);
        verify(userRepository, never()).deleteById(username);

    }
    @Test
    void deleteByIdFailCredentials() {
       authenticatedUser=  new AuthenticatedUser("Myachidsadan",
               "someToken", Role.ROLE_USER.name());
        NotValidCredentialsException actual = assertThrows(NotValidCredentialsException.class,
                () -> userService.deleteById(username, authenticatedUser));

        assertTrue(actual.getMessage().contains("You have no right to make this operations for this data"));
        verify(userRepository,never()).findById(username);
        verify(userRepository, never()).deleteById(username);

    }
    @Test
    void findAll() {
        List<User> userList = List.of(user);
        Pageable pageable = PageRequest.of(1, 3, Sort.by("username"));
        Page<User> page = new PageImpl<>(userList, pageable, 1);
        Page<UserDtoResponse> expected = page.map(user1 -> UserMapper.INSTANCE.convert(user));

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<UserDtoResponse> actual = userService.findAll(pageable);

        assertEquals(expected, actual);
        verify(userRepository).findAll(pageable);
    }

    @Test
    void updatePassword() {
        String password = "newPassword";
        when(userRepository.findById(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(password)).thenReturn(password);
        user.setPassword(password);
        when(userRepository.save(user)).thenReturn(user);

        userService.updatePassword(username,password,authenticatedUser);

        verify(userRepository).findById(username);
        verify(passwordEncoder).encode(password);
    }

    @Test
    void loadUserByUsername() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority((user.getRole().name())));
        org.springframework.security.core.userdetails.User expected =
                new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorities);

        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        UserDetails actual = userService.loadUserByUsername(username);

        assertEquals(expected,actual);
        verify(userRepository).findUserByUsername(username);
    }

    @Test
    void loadUserByUsernameFail() {
        when(userRepository.findUserByUsername(user.getUsername())).thenReturn(Optional.empty());

        NotValidTokenException actual = assertThrows(NotValidTokenException.class,
                () -> userService.loadUserByUsername(user.getUsername()));

        assertTrue(actual.getMessage().contains("Token isn't valid"));
        verify(userRepository).findUserByUsername(user.getUsername());
    }
    @Test
    void getInnerUserByUsername() {
        UserDto expected = userDto;
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.of(user));

        var actual = userService.getInnerUserByUsername(username);

        assertEquals(expected, actual);
        verify(userRepository).findUserByUsername(username);
    }
    @Test
    void getInnerUserByUsernameFail() {
        when(userRepository.findUserByUsername(username)).thenReturn(Optional.empty());

        ResourceNotFoundException actual = assertThrows(ResourceNotFoundException.class,
                () -> userService.getInnerUserByUsername(username));

        assertTrue(actual.getMessage().contains("User wasn't found by username="+username));
        verify(userRepository).findUserByUsername(username);
    }
}