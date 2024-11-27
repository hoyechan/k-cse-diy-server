package com.knucse.diy.domain.exception.key;

import com.knucse.diy.common.exception.support.business.NotFoundException;

public class KeyNotFoundException extends NotFoundException {
    private static final String code = "KEY_NOT_FOUND";
    public KeyNotFoundException() {
        super(code);
    }
}
