package com.knucse.diy.api.reservation.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReservationCancelDto(
        @NotNull List<Long> reservationIds, @NotNull String cancelledReason
) {
}
