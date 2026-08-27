package com.exelynt.booking.reservation.service;

import com.exelynt.booking.reservation.dto.ReservationRequestDTO;
import com.exelynt.booking.reservation.entity.Reservation;
import com.exelynt.booking.reservation.repository.ReservationRepository;
import com.exelynt.booking.reservation.service.impl.ReservationServiceImpl;
import com.exelynt.booking.resource.entity.Resource;
import com.exelynt.booking.resource.repository.ResourceRepository;
import com.exelynt.booking.user.entity.User;
import com.exelynt.booking.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationValidationTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private User user;
    private Resource resource;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(2L);
        user.setUsername("user");

        resource = new Resource();
        resource.setId(2L);
        resource.setName("Vehicle A");
    }

    @Test
    void unavailableResourceShouldBeRejected() {

        resource.setAvailable(false);

        ReservationRequestDTO request =
                new ReservationRequestDTO(
                        2L,
                        LocalDateTime.now().plusDays(1),
                        LocalDateTime.now().plusDays(1).plusHours(2),
                        new BigDecimal("2000.00")
                );

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(resourceRepository.findById(2L))
                .thenReturn(Optional.of(resource));

        assertThrows(
                RuntimeException.class,
                () -> reservationService.createReservation(
                        request,
                        "user"
                )
        );
    }

    @Test
    void startTimeAfterEndTimeShouldBeRejected() {

        ReservationRequestDTO request =
                new ReservationRequestDTO(
                        2L,
                        LocalDateTime.now().plusDays(1).plusHours(3),
                        LocalDateTime.now().plusDays(1).plusHours(2),
                        new BigDecimal("2000.00")
                );

        when(userRepository.findByUsername("user"))
                .thenReturn(Optional.of(user));

        when(resourceRepository.findById(2L))
                .thenReturn(Optional.of(resource));

        assertThrows(
                IllegalArgumentException.class,
                () -> reservationService.createReservation(
                        request,
                        "user"
                )
        );
    }
}