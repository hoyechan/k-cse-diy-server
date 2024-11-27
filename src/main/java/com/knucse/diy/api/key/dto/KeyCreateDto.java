package com.knucse.diy.api.key.dto;

import com.knucse.diy.domain.model.key.Key;
import com.knucse.diy.domain.model.key.KeyStatus;
import jakarta.validation.constraints.NotNull;

public record KeyCreateDto(
        ) {
    public Key toEntity(){
        return Key.builder()
                .holder(null)
                .status(KeyStatus.KEEPING)
                .rentalDateTime(null)
                .returnedDateTime(null)
                .build();
    }
}
