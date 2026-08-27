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

            if (!userRepository.existsByUsername("admin")) {

                User admin = new User();

                admin.setUsername("admin");
                admin.setEmail("admin@exelynt.com");
                admin.setPassword(
                        passwordEncoder.encode("Admin@123")
                );
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
            }

            if (!userRepository.existsByUsername("user")) {

                User user = new User();

                user.setUsername("user");
                user.setEmail("user@exelynt.com");
                user.setPassword(
                        passwordEncoder.encode("User@123")
                );
                user.setRole(Role.USER);

                userRepository.save(user);
            }
            if (!userRepository.existsByUsername("user2")) {

                User user2 = new User();

                user2.setUsername("user2");
                user2.setEmail("user2@exelynt.com");
                user2.setPassword(
                        passwordEncoder.encode("User2@123")
                );
                user2.setRole(Role.USER);

                userRepository.save(user2);
            }
        };
    }
}