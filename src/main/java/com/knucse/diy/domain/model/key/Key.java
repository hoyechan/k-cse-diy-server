package com.knucse.diy.domain.model.key;

import com.knucse.diy.domain.model.student.Student;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "room_key")
public class Key {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "key_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_holder_id") // 현재 소지자
    private Student holder;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_user_id") // 마지막 사용자
    private Student lastUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "key_status", nullable = false)
    private KeyStatus status;

    @Column(name = "returned_datetime")
    private LocalDateTime returnedDateTime;

    @Column(name = "rental_datetime")
    private LocalDateTime rentalDateTime;

    @Builder
    public Key(Student holder,Student lastUser, KeyStatus status, LocalDateTime returnedDateTime, LocalDateTime rentalDateTime) {
        this.holder = holder;
        this.lastUser = lastUser;
        this.status = status;
        this.returnedDateTime = returnedDateTime;
        this.rentalDateTime = rentalDateTime;
    }

    public void updateKey(Student holder, Student lastUser, KeyStatus status, LocalDateTime returnedDateTime, LocalDateTime rentalDateTime) {
        this.holder = holder;
        this.lastUser = lastUser;
        this.status = status;
        this.returnedDateTime = returnedDateTime;
        this.rentalDateTime = rentalDateTime;
    }


}

