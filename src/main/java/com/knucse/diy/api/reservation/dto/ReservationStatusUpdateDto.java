package com.knucse.diy.api.reservation.dto;

import com.knucse.diy.domain.model.reservation.ReservationStatus;
import jakarta.validation.constraints.NotNull;

public record ReservationStatusUpdateDto(
        @NotNull Long reservationId, @NotNull ReservationStatus reservationStatus
) {
}