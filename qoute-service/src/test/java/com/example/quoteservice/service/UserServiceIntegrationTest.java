package com.example.quoteservice.service;

import com.example.quoteservice.dto.UserDto;
import com.example.quoteservice.dto.UserDtoRequest;
import com.example.quoteservice.dto.UserDtoResponse;
import com.example.quoteservice.mapper.UserMapper;
import com.example.quoteservice.model.Role;
import com.example.quoteservice.model.User;
import com.example.quoteservice.security.user.AuthenticatedUser;
import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.configuration.Orthography;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DBRider
@DBUnit(caseInsensitiveStrategy = Orthography.LOWERCASE)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    User user;
    UserDtoResponse expected;
    UserDto innerUserExpected;
    UserDtoRequest userDtoRequest;
    String email;
    String username;
    AuthenticatedUser authenticatedUser;
    @BeforeEach
    void setUp() {
        username="miachyn";
        String email="Someemail@mail.ru";
        user=User.builder()
                .username(username)
                .dateOfBirth(LocalDate.parse("1997-06-25"))
                .email(email)
                .password("somePassword")
                .role(Role.ROLE_ADMIN)
                .build();
        userDtoRequest= UserDtoRequest.builder()
                .email(user.getEmail())
                .dateOfBirth(user.getDateOfBirth())
                .build();
        expected= UserMapper.INSTANCE.convert(user,new ArrayList<>());
        authenticatedUser = new AuthenticatedUser("Myachin",
                "someToken", Role.ROLE_ADMIN.name());
        innerUserExpected = UserMapper.INSTANCE.convertToInner(user);
    }

    @AfterEach
    @DataSet(cleanBefore = true)
    void tearDown() {
        user=null;
        expected=null;
        innerUserExpected=null;
        userDtoRequest=null;
        authenticatedUser=null;
        username=null;
    }

    @Test
    @DataSet(value = {"dataset/init/user/init.yml"},
            useSequenceFiltering = false,
    executeScriptsAfter = {"scripts/cleanUser.sql"})
    @Order(1)
    void getById() {
        UserDtoResponse actual = userService.getById(user.getUsername());

        assertEquals(expected,actual);
    }

    @Test
    @DataSet(value = {"dataset/init/user/init.yml"},
    useSequenceFiltering = false,
    executeScriptsAfter ={"scripts/cleanUser.sql"})
    @ExpectedDataSet(value = {"dataset/expected/user/update.yml"},ignoreCols = {"password","date_of_birth"})
    @Order(2)
    void update() {
        String changedEmail = "SomeAnotheremail@mail.ru";
        userDtoRequest.setEmail(changedEmail);
        expected.setEmail(changedEmail);
        UserDtoResponse actual = userService.update(username, userDtoRequest, authenticatedUser);

        assertEquals(expected,actual);

    }

    @Test
    @DataSet(value = {"dataset/init/user/init.yml"},
            useSequenceFiltering = false,
            executeScriptsAfter ={"scripts/cleanUser.sql"})
    @ExpectedDataSet(value = {"dataset/expected/user/save.yml"},ignoreCols = {"password","date_of_birth"})
    @Order(3)
    void save() {
        UserDtoRequest requestUser = UserDtoRequest.builder()
                .email("Dimakamail@mail.ru")
                .dateOfBirth(LocalDate.of(1997, 06, 25))
                .build();
        User convert = UserMapper.INSTANCE.convert("dimaka", requestUser, "somePassword");
        UserDtoResponse expected = UserMapper.INSTANCE.convert(convert,new ArrayList<>());

        UserDtoResponse actual = userService.save("dimaka", requestUser, "somePassword");

        assertEquals(expected,actual);
    }

    @Test
    @DataSet(value = {"dataset/init/user/init.yml"},
            useSequenceFiltering = false,
            executeScriptsAfter ={"scripts/cleanUser.sql"})
    @ExpectedDataSet(value = {"dataset/expected/user/delete.yml"})
    void deleteById() {
        userService.deleteById(username,authenticatedUser);
    }

    @Test
    @DataSet(value = {"dataset/init/user/init.yml"},
            useSequenceFiltering = false,
            executeScriptsAfter ={"scripts/cleanUser.sql"} )
    @Order(4)
    void findAll() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by("username"));
        Page<UserDtoResponse> page = new PageImpl<>(List.of(expected), pageable, 1);
        Page<UserDtoResponse> actual = userService.findAll(pageable);
        assertEquals(page,actual);
    }

    @Test
    @DataSet(value = {"dataset/init/user/init.yml"},
            useSequenceFiltering = false,
            executeScriptsAfter ={"scripts/cleanUser.sql"})
    @Order(5)
    void loadUserByUsername() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority((user.getRole().name())));
        org.springframework.security.core.userdetails.User expected =
                new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorities);

        UserDetails actual = userService.loadUserByUsername(username);

        assertEquals(expected,actual);

    }

    @Test
    @DataSet(value = {"dataset/init/user/init.yml"},
            useSequenceFiltering = false,
            executeScriptsAfter ={"scripts/cleanUser.sql"} )
    @Order(6)
    void getInnerUserByUsername() {
        UserDto actual = userService.getInnerUserByUsername(username);
        assertEquals(innerUserExpected,actual);
    }
}