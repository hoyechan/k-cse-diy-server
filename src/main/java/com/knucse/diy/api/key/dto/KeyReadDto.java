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
        RoomKeyStatus status
) {
    public static KeyReadDto fromEntity(RoomKey key, Student holder){
        String holderName;
        if(holder == null){
            holderName = "null";
        }else{
            holderName = holder.getStudentName();
        }

        return KeyReadDto.builder()
                .id(key.getId())
                .holderName(holderName)
                .status(key.getStatus())
                .build();
    }
}
