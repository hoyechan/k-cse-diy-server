package com.knucse.diy.api.key.dto;

import com.knucse.diy.domain.model.key.RoomKey;
import com.knucse.diy.domain.model.key.RoomKeyStatus;
import com.knucse.diy.domain.model.student.Student;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record KeyReadDto(
        Long id,
        String holderName,
        RoomKeyStatus status,
        LocalDateTime returnedDateTime,
        LocalDateTime rentalDateTime
) {
    public static KeyReadDto fromEntity(RoomKey key, Student holder){
        return KeyReadDto.builder()
                .id(key.getId())
                .holderName(holder.getStudentName())
                .status(key.getStatus())
                .build();
    }
}
