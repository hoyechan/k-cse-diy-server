package com.knucse.diy.api.key.dto;

import com.knucse.diy.domain.model.key.RoomKeyStatus;
import jakarta.validation.constraints.NotNull;

public record KeyStatusUpdateDto(
        @NotNull String StudentName, @NotNull String StudentNumber,
        @NotNull Long keyId, @NotNull RoomKeyStatus status
        ) {
}
