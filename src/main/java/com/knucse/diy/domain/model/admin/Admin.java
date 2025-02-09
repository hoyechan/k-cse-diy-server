package com.knucse.diy.domain.model.admin;

import com.knucse.diy.domain.model.student.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "admin")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long id;

    @Column(
            name = "username",
            nullable = false,
            length = 15
    )
    private String username;

    @Column(
            name = "password",
            nullable = false,
            length = 70
    )
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Admin(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

}
