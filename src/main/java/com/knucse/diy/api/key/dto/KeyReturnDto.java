package com.knucse.diy.api.key.dto;

import jakarta.validation.constraints.NotNull;

public record KeyReturnDto(
        @NotNull String studentName, @NotNull String studentNumber,
        @NotNull String lockerPassword
) {
}
