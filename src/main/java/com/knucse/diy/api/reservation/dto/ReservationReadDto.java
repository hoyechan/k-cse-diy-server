package com.knucse.diy.api.reservation.dto;

import com.knucse.diy.domain.model.reservation.Reservation;
import com.knucse.diy.domain.model.reservation.ReservationStatus;
import com.knucse.diy.domain.model.student.Student;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record ReservationReadDto(
        Long id,
        String studentName,
        String studentNumber,
        LocalDate reservationDate,
        LocalTime startTime,
        LocalTime endTime,
        String reason,
        ReservationStatus status
) {
    public static ReservationReadDto fromEntity(Reservation reservation) {
        return ReservationReadDto.builder()
                .id(reservation.getId())
                .studentName(reservation.getStudent().getStudentName())
                .studentNumber(reservation.getStudent().getStudentNumber())
                .reservationDate(reservation.getReservationDate())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .reason(reservation.getReason())
                .status(reservation.getStatus())
                .build();
    }
}

