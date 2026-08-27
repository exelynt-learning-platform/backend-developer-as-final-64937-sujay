package com.exelynt.booking.auth.service;

import com.exelynt.booking.auth.dto.LoginRequestDTO;
import com.exelynt.booking.auth.dto.LoginResponseDTO;
import com.exelynt.booking.auth.service.impl.AuthenticationServiceImpl;
import com.exelynt.booking.security.JwtService;
import com.exelynt.booking.user.entity.User;
import com.exelynt.booking.user.enums.Role;
import com.exelynt.booking.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User user;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {

        user = new User();

        user.setId(2L);
        user.setUsername("user");
        user.setEmail("user@exelynt.com");
        user.setRole(Role.USER);

        userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("user")
                        .password("encodedPassword")
                        .roles("USER")
                        .build();
    }

    @Test
    void validLoginShouldGenerateToken() {

        LoginRequestDTO request =
                new LoginRequestDTO();

        request.setUsername("user");
        request.setPassword("User@123");

        when(authenticationManager.authenticate(
                org.mockito.ArgumentMatchers.any(
                        UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        when(authentication.getName())
                .thenReturn("user");

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(jwtService.generateToken(userDetails))
                .thenReturn("test-jwt-token");

        LoginResponseDTO response =
                authenticationService.login(request);

        assertNotNull(response);
    }

    @Test
    void invalidLoginShouldBeRejected() {

        LoginRequestDTO request =
                new LoginRequestDTO();

        request.setUsername("user");
        request.setPassword("WrongPassword");

        when(authenticationManager.authenticate(
                org.mockito.ArgumentMatchers.any(
                        UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
                new BadCredentialsException(
                        "Invalid username or password")
        );

        assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.login(request)
        );
    }
}