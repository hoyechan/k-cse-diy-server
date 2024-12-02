package com.knucse.diy.domain.model.key;

import com.knucse.diy.domain.model.base.BaseTimeEntity;
import com.knucse.diy.domain.model.student.Student;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "room_key")
public class RoomKey extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_key_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id") // 현재 소지자
    private Student holder;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_key_status", nullable = false)
    private RoomKeyStatus status;

    @Builder
    public RoomKey(Student holder, RoomKeyStatus status) {
        this.holder = holder;
        this.status = status;
    }

    public void updateRoomKey(Student holder, RoomKeyStatus status) {
        this.holder = holder;
        this.status = status;
    }
}

