package com.knucse.diy.domain.exception.reservation;

import com.knucse.diy.common.exception.support.business.DuplicatedException;

public class ReservationDuplicatedException extends DuplicatedException {

    private static final String code = "RESERVATION_DUPLICATED";
    public ReservationDuplicatedException() {
        super(code);
    }
}
