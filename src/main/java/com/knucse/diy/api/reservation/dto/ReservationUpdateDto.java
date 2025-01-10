package com.knucse.diy.api.reservation.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationUpdateDto(
        @NotNull Long reservationId,
        @NotNull LocalTime startTime, @NotNull LocalTime endTime,
        @NotNull String reason, @NotNull String authCode
        ) {
}
