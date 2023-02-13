package com.example.quoteservice.service.impl;

import com.example.quoteservice.dto.QuoteDto;
import com.example.quoteservice.dto.UserDto;
import com.example.quoteservice.dto.UserDtoRequest;
import com.example.quoteservice.dto.UserDtoResponse;
import com.example.quoteservice.exception.NotUniqueResourceException;
import com.example.quoteservice.exception.NotValidTokenException;
import com.example.quoteservice.exception.ResourceNotFoundException;
import com.example.quoteservice.mapper.QuoteMapper;
import com.example.quoteservice.mapper.UserMapper;
import com.example.quoteservice.model.User;
import com.example.quoteservice.repository.UserRepository;
import com.example.quoteservice.security.user.AuthenticatedUser;
import com.example.quoteservice.service.UserService;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.quoteservice.util.ConstantUtil.ValidOperations.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Cacheable(value = "user", key = "#username")
    public UserDtoResponse getById(String username) {
        return userRepository.findById(username)
                .map(this::getUserDto)
                .orElseThrow(()->new ResourceNotFoundException(User.class,"username",username));
    }

    @Override
    @Transactional
    @CachePut(value = "user",key = "#username")
    public UserDtoResponse update(String username, UserDtoRequest userDtoRequest, AuthenticatedUser authenticatedUser) {
        checkValidCredentials(authenticatedUser,username);
        return userRepository.findById(username)
                .map(user -> {
                    if (!user.getEmail().equals(userDtoRequest.getEmail()))
                        existsByEmail(userDtoRequest.getEmail());
                    return UserMapper.INSTANCE.convert(user.getUsername(),userDtoRequest,user.getPassword(),user.getRole(),user.getQuotes());})
                .map(userRepository::save)
                .map(this::getUserDto)
                .orElseThrow(()->new ResourceNotFoundException(User.class,"username",username));
    }

    @Override
    @Transactional
    public UserDtoResponse save(String username, UserDtoRequest userDtoRequest, String password) {
        existsByUsernameAndEmail(username,userDtoRequest.getEmail());

        password= passwordEncoder.encode(password);
        User user = UserMapper.INSTANCE.convert(username,userDtoRequest, password);
        return getUserDto(userRepository.save(user));
    }

    private void existsByUsernameAndEmail(String username,  String email) {
        existsByUsername(username);
        existsByEmail(email);
    }

    private void existsByEmail(String email) {
        if (!userRepository.existsByEmail(email))
            return;
        throw new NotUniqueResourceException(User.class,"email",email);
    }

    @Override
    @Transactional
    @Caching(evict={
            @CacheEvict(value ="user",key = "#username"),
            @CacheEvict(value = "quote"),
            @CacheEvict(value = "innerUser",key = "#username")})
    public void deleteById(String username, AuthenticatedUser authenticatedUser) {
        checkValidCredentials(authenticatedUser,username);
        UserDtoResponse user = getById(username);
        userRepository.deleteById(user.getUsername());
    }

    @Override
    public Page<UserDtoResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(this::getUserDto);
    }

    @Override
    public void existsByUsername(String username) {
        if(!userRepository.existsUserByUsername(username))
            return;
        throw new NotUniqueResourceException(User.class,"username",username);

    }

    @Override
    @Transactional
    @CachePut(value = "user",key = "#username")
    public void updatePassword(String username, String password, AuthenticatedUser authenticatedUser) {
        checkValidCredentials(authenticatedUser,username);
        Optional<User> optionalUser = userRepository.findById(username);
        password= passwordEncoder.encode(password);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setPassword(password);
            userRepository.save(user);
            return;
        }
        throw new ResourceNotFoundException(User.class,"username",username);
    }



    private UserDtoResponse getUserDto(User user) {

        List<QuoteDto> quotes= user.getQuotes().stream()
                .map(QuoteMapper.INSTANCE::convert)
                .collect(Collectors.toList());
        return UserMapper.INSTANCE.convert(user,quotes);
    }

    @Override
    @Cacheable(value = "innerUser",key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto user = userRepository.findUserByUsername(username)
                .map(UserMapper.INSTANCE::convertToInner)
                .orElseThrow(()->new NotValidTokenException("Token isn't valid"));
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority((user.getRole().name())));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), authorities
        );
    }

    @Override
    public UserDto getInnerUserByUsername(String username) {
        return userRepository.findUserByUsername(username)
                .map(UserMapper.INSTANCE::convertToInner)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, "username", username));
    }
}
