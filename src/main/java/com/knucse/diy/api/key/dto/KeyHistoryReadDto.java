package com.knucse.diy.api.key.dto;

import com.knucse.diy.domain.model.key.RoomKeyHistory;
import com.knucse.diy.domain.model.key.RoomKeyStatus;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record KeyHistoryReadDto(
        Long id,
        String studentName,
        String studentNumber,
        LocalDateTime date,
        RoomKeyStatus status
) {
    public static KeyHistoryReadDto fromEntity(RoomKeyHistory roomKeyHistory) {
        return KeyHistoryReadDto.builder()
                .id(roomKeyHistory.getId())
                .studentName(roomKeyHistory.getStudentName())
                .studentNumber(roomKeyHistory.getStudentNumber())
                .date(roomKeyHistory.getDate())
                .status(roomKeyHistory.getStatus())
                .build();
    }
}
