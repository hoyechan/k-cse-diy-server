package com.knucse.diy.domain.model.reservation;

import com.knucse.diy.api.reservation.dto.ReservationUpdateDto;
import com.knucse.diy.domain.model.base.BaseTimeEntity;
import com.knucse.diy.domain.model.student.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservation")
public class Reservation extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // 예약한 학생 정보

    @Column(name = "auth_code")
    private String authCode;

    @Column(name = "reservation_date", nullable = false)
    private LocalDate reservationDate; // 예약 날짜

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // 예약 시작 시간

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime; // 예약 종료 시간

    @Column(name = "reason",
            nullable = false,
            length = 50
    )
    private String reason; //예약 사유

    @Column(name = "cancelled_reason",
            nullable = true,
            length = 50
    )
    private String cancelledReason; //예약 거절 사유

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReservationStatus status; // 예약 상태 (대기, 승인, 취소)

    @Builder
    public Reservation(Student student, LocalDate reservationDate, LocalTime startTime, LocalTime endTime,String reason,String authCode, ReservationStatus status, String cancelledReason) {
        this.student = student;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.authCode = authCode;
        this.status = status;
        this.cancelledReason = cancelledReason;
    }

    public void updateStatus(ReservationStatus status) {
        this.status = status;
    }

    public void updateAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public void cancelReservation(ReservationStatus status, String cancelledReason) {
        this.status = status;
        this.cancelledReason = cancelledReason;
    }

    public void updateReservation(ReservationUpdateDto updateDto){
        this.startTime = updateDto.startTime();
        this.endTime = updateDto.endTime();
        this.reason = updateDto.reason();
        this.status = ReservationStatus.PENDING;
    }
}

