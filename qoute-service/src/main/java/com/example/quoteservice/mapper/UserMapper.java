package com.example.quoteservice.mapper;

import com.example.quoteservice.dto.*;
import com.example.quoteservice.model.Quote;
import com.example.quoteservice.model.Role;
import com.example.quoteservice.model.User;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);


    @Mappings({
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "user.dateOfBirth", target = "dateOfBirth"),
            @Mapping(source = "password", target = "password"),
            @Mapping(target = "quotes",expression = "java(new ArrayList())"),
            @Mapping(target = "role", expression = "java(Role.ROLE_USER)"),
            @Mapping(source = "user.email",target = "email")
    })
    User convert(String username, UserDtoRequest user, String password);

    @Mappings({
            @Mapping(source = "username", target = "username"),
            @Mapping(source = "user.dateOfBirth", target = "dateOfBirth"),
            @Mapping(source = "password", target = "password"),
            @Mapping(source="quotes",target = "quotes"),
            @Mapping(source="role", target = "role"),
            @Mapping(source = "user.email",target = "email")
    })
    User convert(String username,UserDtoRequest user, String password,Role role,List<Quote> quotes);

    @Mappings({
            @Mapping(source = "user.username",target = "username"),
            @Mapping(source = "user.dateOfBirth",target = "dateOfBirth"),
            @Mapping(source = "quotes",target = "quotes"),
            @Mapping(source = "user.email",target = "email")
    })
    UserDtoResponse convert(User user, List<QuoteDto> quotes);

    UserDtoResponse convert(User user);


    UserDto convertToInner(User user);

    @Mappings({
     @Mapping(source = "userDto.username",target = "username")
    })
    User convert(UserDto userDto);

}
