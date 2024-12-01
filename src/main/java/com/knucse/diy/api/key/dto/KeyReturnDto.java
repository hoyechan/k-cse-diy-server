package com.knucse.diy.api.key.dto;

import jakarta.validation.constraints.NotNull;

public record KeyReturnDto(
        @NotNull String StudentName, @NotNull String StudentNumber,
        @NotNull String lockerPassword
) {
}
