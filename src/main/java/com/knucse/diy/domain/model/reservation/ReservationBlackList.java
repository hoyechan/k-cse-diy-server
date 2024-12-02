package com.knucse.diy.domain.model.reservation;

import com.knucse.diy.domain.model.base.BaseTimeEntity;
import com.knucse.diy.domain.model.student.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "reservation_black_list")
public class ReservationBlackList extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student; // 예약한 학생 정보

    @Builder
    public ReservationBlackList(Student student) {
        this.student = student;
    }

}
