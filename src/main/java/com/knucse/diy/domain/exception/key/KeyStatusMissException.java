package com.knucse.diy.domain.exception.key;

import com.knucse.diy.common.exception.support.business.BadRequestException;

public class KeyStatusMissException extends BadRequestException {

    private static final String code = "KEY_STATUS_MISS_EXCEPTION";

    public KeyStatusMissException() {
        super(code);
    }
}
