package com.exelynt.booking.auth.service.impl;

import com.exelynt.booking.auth.dto.LoginRequestDTO;
import com.exelynt.booking.auth.dto.LoginResponseDTO;
import com.exelynt.booking.auth.service.AuthenticationService;
import com.exelynt.booking.security.JwtService;
import com.exelynt.booking.user.entity.User;
import com.exelynt.booking.user.exception.UserNotFoundException;
import com.exelynt.booking.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public AuthenticationServiceImpl(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        if (!(authentication.getPrincipal()
                instanceof UserDetails userDetails)) {

            throw new IllegalStateException(
                    "Authenticated principal is not a UserDetails instance"
            );
        }

        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));

        String token =
                jwtService.generateToken(userDetails);

        return new LoginResponseDTO(
                token,
                user.getUsername(),
                user.getRole()
        );
    }
}