package com.exelynt.booking.reservation.service;

import com.exelynt.booking.reservation.dto.ReservationRequestDTO;
import com.exelynt.booking.reservation.dto.ReservationResponseDTO;
import com.exelynt.booking.reservation.enums.ReservationStatus;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

public interface ReservationService {

    ReservationResponseDTO createReservation(
            ReservationRequestDTO request,
            String username
    );

    Page<ReservationResponseDTO> getReservations(
            String username,
            boolean admin,
            String status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy,
            String sortDirection
    );

    ReservationResponseDTO getReservationById(
            Long id,
            String username,
            boolean admin
    );

    ReservationResponseDTO updateReservation(
            Long id,
            ReservationRequestDTO request,
            String username,
            boolean admin
    );

    void deleteReservation(
            Long id,
            String username,
            boolean admin
    );
    ReservationResponseDTO updateReservationStatus(
            Long id,
            ReservationStatus status
    );
}