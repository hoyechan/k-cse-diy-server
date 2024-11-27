package com.knucse.diy.domain.exception.authcode;

import com.knucse.diy.common.exception.support.business.BadRequestException;

public class AuthCodeBadRequestException extends BadRequestException {
    private static final String code = "AUTHENTICATION CODE MUST BE 4 DIGITS";

    public AuthCodeBadRequestException() {
        super(code);
    }
}
