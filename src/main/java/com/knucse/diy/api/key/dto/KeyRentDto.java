package com.knucse.diy.api.key.dto;

import com.knucse.diy.domain.model.student.Student;
import jakarta.validation.constraints.NotNull;

public record KeyRentDto(
        @NotNull String studentName, @NotNull String studentNumber
        ) {
}
