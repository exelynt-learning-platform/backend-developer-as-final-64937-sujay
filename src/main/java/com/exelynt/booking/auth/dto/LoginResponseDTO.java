package com.exelynt.booking.auth.dto;

import com.exelynt.booking.user.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String username;
    private Role role;
}