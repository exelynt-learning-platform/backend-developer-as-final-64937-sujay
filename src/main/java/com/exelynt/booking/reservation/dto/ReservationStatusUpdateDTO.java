package com.exelynt.booking.reservation.dto;

import com.exelynt.booking.reservation.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationStatusUpdateDTO {

    @NotNull(message = "Reservation status is required")
    private ReservationStatus status;
}