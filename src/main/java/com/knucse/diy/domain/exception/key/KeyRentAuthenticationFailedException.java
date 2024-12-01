package com.knucse.diy.domain.exception.key;

import com.knucse.diy.common.exception.support.security.AuthenticationFailedException;

public class KeyRentAuthenticationFailedException extends AuthenticationFailedException {

    private static final String code = "KEY_RENT_AUTHENTICATION_FAILED";
    public KeyRentAuthenticationFailedException() {
        super(code);
    }
}
