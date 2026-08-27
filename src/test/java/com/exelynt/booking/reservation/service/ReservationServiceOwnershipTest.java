package com.exelynt.booking.reservation.service;

import com.exelynt.booking.reservation.dto.ReservationRequestDTO;
import com.exelynt.booking.reservation.entity.Reservation;
import com.exelynt.booking.reservation.enums.ReservationStatus;
import com.exelynt.booking.reservation.exception.ReservationNotFoundException;
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
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceOwnershipTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ResourceRepository resourceRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private Reservation reservation;
    private User user;
    private User user2;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setId(2L);
        user.setUsername("user");
        user.setEmail("user@exelynt.com");

        user2 = new User();
        user2.setId(3L);
        user2.setUsername("user2");
        user2.setEmail("user2@exelynt.com");

        Resource resource = new Resource();
        resource.setId(2L);
        resource.setName("Vehicle A");

        reservation = new Reservation();
        reservation.setId(2L);
        reservation.setUser(user2);
        reservation.setResource(resource);
    }

    @Test
    void userCanAccessOwnReservation() {

        reservation.setUser(user);

        when(reservationRepository.findById(2L))
                .thenReturn(Optional.of(reservation));

        assertDoesNotThrow(() ->
                reservationService.getReservationById(
                        2L,
                        "user",
                        false
                )
        );
    }

    @Test
    void userCannotAccessAnotherUsersReservation() {

        when(reservationRepository.findById(2L))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                AccessDeniedException.class,
                () ->
                        reservationService.getReservationById(
                                2L,
                                "user",
                                false
                        )
        );
    }

    @Test
    void adminCanAccessAnotherUsersReservation() {

        when(reservationRepository.findById(2L))
                .thenReturn(Optional.of(reservation));

        assertDoesNotThrow(() ->
                reservationService.getReservationById(
                        2L,
                        "admin",
                        true
                )
        );
    }

    @Test
    void reservationNotFoundThrowsException() {

        when(reservationRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ReservationNotFoundException.class,
                () ->
                        reservationService.getReservationById(
                                99L,
                                "user",
                                false
                        )
        );
    }

    @Test
    void userCannotUpdateAnotherUsersReservation() {

        when(reservationRepository.findById(2L))
                .thenReturn(Optional.of(reservation));

        ReservationRequestDTO request =
                new ReservationRequestDTO(
                        2L,
                        java.time.LocalDateTime.now().plusDays(1),
                        java.time.LocalDateTime.now().plusDays(1).plusHours(2),
                        new java.math.BigDecimal("2000.00")
                );

        assertThrows(
                AccessDeniedException.class,
                () ->
                        reservationService.updateReservation(
                                2L,
                                request,
                                "user",
                                false
                        )
        );
    }
    @Test
    void userCannotDeleteAnotherUsersReservation() {

        when(reservationRepository.findById(2L))
                .thenReturn(Optional.of(reservation));

        assertThrows(
                AccessDeniedException.class,
                () ->
                        reservationService.deleteReservation(
                                2L,
                                "user",
                                false
                        )
        );
    }

    @Test
    void adminCanUpdateReservationStatus() {

        reservation.setStatus(ReservationStatus.PENDING);

        when(reservationRepository.findById(2L))
                .thenReturn(Optional.of(reservation));

        when(reservationRepository.save(reservation))
                .thenReturn(reservation);

        var response =
                reservationService.updateReservationStatus(
                        2L,
                        ReservationStatus.CONFIRMED
                );

        org.junit.jupiter.api.Assertions.assertEquals(
                ReservationStatus.CONFIRMED,
                response.getStatus()
        );
    }
}