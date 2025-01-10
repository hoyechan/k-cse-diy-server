package com.knucse.diy.api.key.dto;

import com.knucse.diy.domain.model.key.RoomKeyStatus;
import jakarta.validation.constraints.NotNull;

public record KeyStatusUpdateDto(
        @NotNull String studentName, @NotNull String studentNumber,
        @NotNull Long keyId, @NotNull RoomKeyStatus status
        ) {
}
