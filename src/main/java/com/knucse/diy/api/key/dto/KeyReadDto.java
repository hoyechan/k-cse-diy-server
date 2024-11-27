package com.knucse.diy.api.key.dto;

import com.knucse.diy.domain.model.key.Key;
import com.knucse.diy.domain.model.key.KeyStatus;
import com.knucse.diy.domain.model.student.Student;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record KeyReadDto(
        Long id,
        String holderName,
        KeyStatus status,
        LocalDateTime returnedDateTime,
        LocalDateTime rentalDateTime
) {
    public static KeyReadDto fromEntity(Key key, Student holder){
        return KeyReadDto.builder()
                .id(key.getId())
                .holderName(holder.getStudentName())
                .status(key.getStatus())
                .returnedDateTime(key.getReturnedDateTime())
                .rentalDateTime(key.getRentalDateTime())
                .build();
    }
}
