package com.knucse.diy.api.key.dto;

import com.knucse.diy.domain.model.key.RoomKey;
import com.knucse.diy.domain.model.key.RoomKeyStatus;

public record KeyCreateDto(
        ) {
    public RoomKey toEntity(){
        return RoomKey.builder()
                .holder(null)
                .status(RoomKeyStatus.KEEPING)
                .build();
    }
}
