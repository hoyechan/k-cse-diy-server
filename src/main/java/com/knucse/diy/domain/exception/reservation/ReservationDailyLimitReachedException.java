package com.knucse.diy.domain.exception.reservation;

import com.knucse.diy.common.exception.support.business.DuplicatedException;

public class ReservationDailyLimitReachedException extends DuplicatedException {

    private static final String code = "DAILY_LIMIT_REACHED";

    public ReservationDailyLimitReachedException() {
        super(code);
    }
}
