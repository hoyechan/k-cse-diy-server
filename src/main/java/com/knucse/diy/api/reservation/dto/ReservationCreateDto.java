package com.knucse.diy.api.reservation.dto;

import com.knucse.diy.domain.model.reservation.Reservation;
import com.knucse.diy.domain.model.reservation.ReservationStatus;
import com.knucse.diy.domain.model.student.Student;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationCreateDto(
        @NotNull String studentName, @NotNull String studentNumber,
        @NotNull LocalDate reservationDate,
        @NotNull LocalTime startTime, @NotNull LocalTime endTime,
        @NotNull String reason, @NotNull String authCode
) {
    public Reservation toEntity(Student student, String hashedAuthCode){
        return Reservation.builder()
                .student(student)
                .reservationDate(reservationDate)
                .startTime(startTime)
                .endTime(endTime)
                .reason(reason)
                .authCode(hashedAuthCode)
                .status(ReservationStatus.PENDING)
                .cancelledReason(null)
                .build();
    }
}
