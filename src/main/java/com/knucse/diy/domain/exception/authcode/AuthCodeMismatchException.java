package com.knucse.diy.domain.exception.authcode;

import com.knucse.diy.common.exception.support.security.AuthenticationFailedException;

public class AuthCodeMismatchException extends AuthenticationFailedException {

    private static final String code = "AUTHENTICATION CODE MISMATCH";

    public AuthCodeMismatchException() {
        super(code);
    }
}
