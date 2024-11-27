package com.knucse.diy.domain.exception.key;

import com.knucse.diy.common.exception.support.business.DuplicatedException;

public class KeyDuplicatedException extends DuplicatedException {

    private static final String code = "KEY_DUPLICATED";
    public KeyDuplicatedException() {
        super(code);
    }
}
