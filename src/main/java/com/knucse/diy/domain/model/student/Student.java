package com.knucse.diy.domain.model.student;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "student")
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_id")
    private Long id;

    @Column(
        name = "student_number",
        nullable = false,
        length = 15,
        unique = true
    )
    private String studentNumber;

    @Column(
        name = "name",
        length = 50
    )
    private String studentName;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Student(String studentNumber, String studentName, Role role) {
        this.studentNumber = studentNumber;
        this.studentName = studentName;
        this.role = role;
    }
}
