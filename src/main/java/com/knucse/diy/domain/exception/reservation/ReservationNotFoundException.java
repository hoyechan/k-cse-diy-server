package com.knucse.diy.domain.exception.reservation;

import com.knucse.diy.common.exception.support.business.NotFoundException;

public class ReservationNotFoundException extends NotFoundException {

    private static final String code = "RESERVATION_NOT_FOUND";
    public ReservationNotFoundException() {
        super(code);
    }
}
