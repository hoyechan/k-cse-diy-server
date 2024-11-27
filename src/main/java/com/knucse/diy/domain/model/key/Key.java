package com.knucse.diy.domain.model.key;

import com.knucse.diy.domain.model.student.Student;
import jakarta.persistence.*;
import lombok.*;

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
    @JoinColumn(name = "student_id")
    private Student holder;

    @Enumerated(EnumType.STRING)
    @Column(name = "key_status", nullable = false)
    private KeyStatus status;

    @Column(name = "returned_datetime")
    private LocalDateTime returnedDateTime;

    @Column(name = "rental_datetime")
    private LocalDateTime rentalDateTime;

    @Builder
    public Key(Student holder, KeyStatus status, LocalDateTime returnedDateTime, LocalDateTime rentalDateTime) {
        this.holder = holder;
        this.status = status;
        this.returnedDateTime = returnedDateTime;
        this.rentalDateTime = rentalDateTime;
    }

    public void updateStatus(KeyStatus status) {
        this.status = status;
    }


}

