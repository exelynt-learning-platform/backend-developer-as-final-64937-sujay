package com.exelynt.booking.reservation.controller;

import com.exelynt.booking.reservation.dto.ReservationRequestDTO;
import com.exelynt.booking.reservation.dto.ReservationResponseDTO;
import com.exelynt.booking.reservation.dto.ReservationStatusUpdateDTO;
import com.exelynt.booking.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @Valid @RequestBody ReservationRequestDTO request,
            Authentication authentication) {

        String username = authentication.getName();

        ReservationResponseDTO response =
                reservationService.createReservation(request, username);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<Page<ReservationResponseDTO>> getReservations(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection) {

        String username = authentication.getName();

        boolean admin = isAdmin(authentication);

        Page<ReservationResponseDTO> reservations =
                reservationService.getReservations(
                        username,
                        admin,
                        status,
                        minPrice,
                        maxPrice,
                        page,
                        size,
                        sortBy,
                        sortDirection
                );

        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> getReservationById(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();

        boolean admin = isAdmin(authentication);

        return ResponseEntity.ok(
                reservationService.getReservationById(
                        id,
                        username,
                        admin
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationRequestDTO request,
            Authentication authentication) {

        String username = authentication.getName();

        boolean admin = isAdmin(authentication);

        return ResponseEntity.ok(
                reservationService.updateReservation(
                        id,
                        request,
                        username,
                        admin
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();

        boolean admin = isAdmin(authentication);

        reservationService.deleteReservation(
                id,
                username,
                admin
        );

        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<ReservationResponseDTO> updateReservationStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReservationStatusUpdateDTO request) {

        return ResponseEntity.ok(
                reservationService.updateReservationStatus(
                        id,
                        request.getStatus()
                )
        );
    }
    private boolean isAdmin(Authentication authentication) {

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));
    }
}