package com.exelynt.booking.auth.controller;

import com.exelynt.booking.auth.dto.LoginRequestDTO;
import com.exelynt.booking.auth.dto.LoginResponseDTO;
import com.exelynt.booking.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(
            AuthenticationService authenticationService) {

        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {

        return ResponseEntity.ok(
                authenticationService.login(request)
        );
    }
}