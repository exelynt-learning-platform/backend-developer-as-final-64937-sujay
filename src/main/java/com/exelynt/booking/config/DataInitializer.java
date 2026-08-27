package com.exelynt.booking.config;

import com.exelynt.booking.user.entity.User;
import com.exelynt.booking.user.enums.Role;
import com.exelynt.booking.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            createUserIfNotExists(
                    userRepository,
                    passwordEncoder,
                    "admin",
                    "admin@exelynt.com",
                    "Admin@123",
                    Role.ADMIN
            );

            createUserIfNotExists(
                    userRepository,
                    passwordEncoder,
                    "user",
                    "user@exelynt.com",
                    "User@123",
                    Role.USER
            );

            createUserIfNotExists(
                    userRepository,
                    passwordEncoder,
                    "user2",
                    "user2@exelynt.com",
                    "User2@123",
                    Role.USER
            );
        };
    }

    private void createUserIfNotExists(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String username,
            String email,
            String password,
            Role role) {

        if (!userRepository.existsByUsername(username)) {

            User user = new User();

            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(
                    passwordEncoder.encode(password)
            );
            user.setRole(role);

            userRepository.save(user);
        }
    }
}