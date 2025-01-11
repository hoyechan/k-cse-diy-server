package com.knucse.diy.api.reservation.dto;

import jakarta.validation.constraints.NotNull;

public record ReservationCancelDto(
        @NotNull Long reservationId, @NotNull String cancelledReason
) {
}
