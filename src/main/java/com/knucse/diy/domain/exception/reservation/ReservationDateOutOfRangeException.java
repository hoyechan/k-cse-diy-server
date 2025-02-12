package com.knucse.diy.domain.exception.reservation;

import com.knucse.diy.common.exception.support.business.BadRequestException;

public class ReservationDateOutOfRangeException extends BadRequestException {

    private static final String code = "RESERVATION_DATE_OUT_OF_RANGE";

    public ReservationDateOutOfRangeException() {
        super(code);
    }
}
