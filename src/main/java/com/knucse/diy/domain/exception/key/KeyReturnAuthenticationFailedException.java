package com.knucse.diy.domain.exception.key;

import com.knucse.diy.common.exception.support.security.AuthenticationFailedException;

public class KeyReturnAuthenticationFailedException extends AuthenticationFailedException {

    private static final String code = "KEY_RETURN_AUTHENTICATION_FAILED";
    public KeyReturnAuthenticationFailedException() {
        super(code);
    }
}
