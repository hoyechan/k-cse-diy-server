package com.knucse.diy.api.reservation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record ReservationDeleteDto(
        @NotNull Long reservationId,
        @NotNull String authCode
) {
}
