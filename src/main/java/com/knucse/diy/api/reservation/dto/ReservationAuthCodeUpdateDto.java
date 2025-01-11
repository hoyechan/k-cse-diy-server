package com.knucse.diy.api.reservation.dto;

import jakarta.validation.constraints.NotNull;

public record ReservationAuthCodeUpdateDto(
        @NotNull Long reservationId, @NotNull String newAuthCode
) {
}
