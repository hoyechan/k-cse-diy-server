package com.knucse.diy.domain.exception.student;

import com.knucse.diy.common.exception.support.business.NotFoundException;

public class StudentNotFoundException extends NotFoundException {
    private static final String code = "STUDENT_NOT_FOUND";

    public StudentNotFoundException() {
        super(code);
    }
}
