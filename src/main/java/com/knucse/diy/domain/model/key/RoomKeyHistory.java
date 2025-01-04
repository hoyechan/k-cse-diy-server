package com.knucse.diy.domain.model.key;

import com.knucse.diy.domain.model.base.BaseTimeEntity;
import com.knucse.diy.domain.model.student.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "room_key_history")
public class RoomKeyHistory extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "student_number")
    private String studentNumber;

    @Column(name = "datetime")
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_key_status", nullable = false)
    private RoomKeyStatus status;

    @Builder
    public RoomKeyHistory(Student student, LocalDateTime date, RoomKeyStatus status) {
        this.studentName = student.getStudentName();
        this.studentNumber = student.getStudentNumber();
        this.date = date;
        this.status = status;
    }
}

