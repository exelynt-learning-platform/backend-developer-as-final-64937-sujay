package com.exelynt.booking.reservation.service.impl;

import com.exelynt.booking.exception.BusinessException;
import com.exelynt.booking.reservation.dto.ReservationRequestDTO;
import com.exelynt.booking.reservation.dto.ReservationResponseDTO;
import com.exelynt.booking.reservation.entity.Reservation;
import com.exelynt.booking.reservation.enums.ReservationStatus;
import com.exelynt.booking.reservation.exception.ReservationNotFoundException;
import com.exelynt.booking.reservation.repository.ReservationRepository;
import com.exelynt.booking.reservation.service.ReservationService;
import com.exelynt.booking.reservation.specification.ReservationSpecification;
import com.exelynt.booking.resource.entity.Resource;
import com.exelynt.booking.resource.exception.ResourceNotFoundException;
import com.exelynt.booking.resource.repository.ResourceRepository;
import com.exelynt.booking.user.entity.User;
import com.exelynt.booking.user.exception.UserNotFoundException;
import com.exelynt.booking.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            UserRepository userRepository,
            ResourceRepository resourceRepository) {

        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.resourceRepository = resourceRepository;
    }

    @Override
    public ReservationResponseDTO createReservation(
            ReservationRequestDTO request,
            String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        Resource resource = resourceRepository.findById(
                        request.getResourceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resource not found with id: "
                                        + request.getResourceId()));

        validateReservationTimes(
                request.getStartTime(),
                request.getEndTime()
        );

        if (!resource.isAvailable()) {
            throw new BusinessException(
                    "Resource is not available");
        }

        Reservation reservation = new Reservation();

        reservation.setUser(user);

        reservation.setResource(resource);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setPrice(request.getPrice());

        reservation.setStatus(ReservationStatus.PENDING);

        Reservation savedReservation =
                reservationRepository.save(reservation);

        return mapToResponse(savedReservation);
    }

    @Override
    public Page<ReservationResponseDTO> getReservations(
            String username,
            boolean admin,
            String status,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        validatePagination(page, size);

        if (minPrice != null
                && maxPrice != null
                && minPrice.compareTo(maxPrice) > 0) {

            throw new IllegalArgumentException(
                    "Minimum price cannot be greater than maximum price");
        }

        Sort sort = createSort(sortBy, sortDirection);

        Pageable pageable =
                PageRequest.of(page, size, sort);

        Specification<Reservation> specification = null;


        if (!admin) {
            specification =
                    ReservationSpecification.hasUsername(username);
        }

        if (status != null && !status.isBlank()) {

            ReservationStatus reservationStatus;

            try {
                reservationStatus =
                        ReservationStatus.valueOf(
                                status.toUpperCase());

            } catch (IllegalArgumentException exception) {

                throw new IllegalArgumentException(
                        "Invalid reservation status: " + status);
            }

            specification =
                    specification == null
                            ? ReservationSpecification.hasStatus(
                            reservationStatus)
                            : specification.and(
                            ReservationSpecification.hasStatus(
                                    reservationStatus));
        }

        if (minPrice != null) {

            specification =
                    specification == null
                            ? ReservationSpecification
                            .priceGreaterThanOrEqualTo(minPrice)
                            : specification.and(
                            ReservationSpecification
                                    .priceGreaterThanOrEqualTo(
                                            minPrice));
        }

        if (maxPrice != null) {

            specification =
                    specification == null
                            ? ReservationSpecification
                            .priceLessThanOrEqualTo(maxPrice)
                            : specification.and(
                            ReservationSpecification
                                    .priceLessThanOrEqualTo(
                                            maxPrice));
        }

        return reservationRepository
                .findAll(specification, pageable)
                .map(this::mapToResponse);
    }

    @Override
    public ReservationResponseDTO getReservationById(
            Long id,
            String username,
            boolean admin) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new ReservationNotFoundException(
                                        "Reservation not found with id: "
                                                + id));


        validateOwnership(
                reservation,
                username,
                admin
        );

        return mapToResponse(reservation);
    }

    @Override
    public ReservationResponseDTO updateReservation(
            Long id,
            ReservationRequestDTO request,
            String username,
            boolean admin) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new ReservationNotFoundException(
                                        "Reservation not found with id: "
                                                + id));


        validateOwnership(
                reservation,
                username,
                admin
        );

        Resource resource =
                resourceRepository.findById(
                                request.getResourceId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Resource not found with id: "
                                                + request.getResourceId()));

        validateReservationTimes(
                request.getStartTime(),
                request.getEndTime()
        );

        if (!resource.isAvailable()) {
            throw new BusinessException(
                    "Resource is not available");
        }

        reservation.setResource(resource);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(request.getEndTime());
        reservation.setPrice(request.getPrice());

        Reservation updatedReservation =
                reservationRepository.save(reservation);

        return mapToResponse(updatedReservation);
    }

    @Override
    public void deleteReservation(
            Long id,
            String username,
            boolean admin) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new ReservationNotFoundException(
                                        "Reservation not found with id: "
                                                + id));

        validateOwnership(
                reservation,
                username,
                admin
        );

        reservationRepository.delete(reservation);
    }


    private void validateOwnership(
            Reservation reservation,
            String username,
            boolean admin) {

        if (admin) {
            return;
        }

        String reservationOwner =
                reservation.getUser().getUsername();

        if (!reservationOwner.equals(username)) {

            throw new AccessDeniedException(
                    "You are not authorized to access this reservation");
        }
    }

    private void validateReservationTimes(
            LocalDateTime startTime,
            LocalDateTime endTime) {

        if (!startTime.isBefore(endTime)) {

            throw new IllegalArgumentException(
                    "Start time must be before end time");
        }

        if (!startTime.isAfter(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "Start time must be in the future");
        }

        if (!endTime.isAfter(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "End time must be in the future");
        }
    }

    private void validatePagination(
            int page,
            int size) {

        if (page < 0) {

            throw new IllegalArgumentException(
                    "Page must be greater than or equal to 0");
        }

        if (size < 1 || size > 100) {

            throw new IllegalArgumentException(
                    "Size must be between 1 and 100");
        }
    }

    private Sort createSort(
            String sortBy,
            String sortDirection) {

        if (sortBy == null || sortBy.isBlank()) {

            return Sort.by("createdAt").descending();
        }

        Sort.Direction direction =
                "desc".equalsIgnoreCase(sortDirection)
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        return Sort.by(direction, sortBy);
    }

    private ReservationResponseDTO mapToResponse(
            Reservation reservation) {

        ReservationResponseDTO response =
                new ReservationResponseDTO();

        response.setId(reservation.getId());

        response.setUserId(
                reservation.getUser().getId());

        response.setUsername(
                reservation.getUser().getUsername());

        response.setResourceId(
                reservation.getResource().getId());

        response.setResourceName(
                reservation.getResource().getName());

        response.setStartTime(
                reservation.getStartTime());

        response.setEndTime(
                reservation.getEndTime());

        response.setPrice(
                reservation.getPrice());

        response.setStatus(
                reservation.getStatus());

        response.setCreatedAt(
                reservation.getCreatedAt());

        response.setUpdatedAt(
                reservation.getUpdatedAt());

        return response;
    }

    @Override
    public ReservationResponseDTO updateReservationStatus(
            Long id,
            ReservationStatus status) {

        Reservation reservation =
                reservationRepository.findById(id)
                        .orElseThrow(() ->
                                new ReservationNotFoundException(
                                        "Reservation not found with id: "
                                                + id));

        reservation.setStatus(status);

        Reservation updatedReservation =
                reservationRepository.save(reservation);

        return mapToResponse(updatedReservation);
    }

}