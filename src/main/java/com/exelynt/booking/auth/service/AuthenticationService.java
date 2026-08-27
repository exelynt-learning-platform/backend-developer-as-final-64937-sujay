package com.exelynt.booking.auth.service;

import com.exelynt.booking.auth.dto.LoginRequestDTO;
import com.exelynt.booking.auth.dto.LoginResponseDTO;

public interface AuthenticationService {

    LoginResponseDTO login(LoginRequestDTO request);
}