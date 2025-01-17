package com.knucse.diy.domain.model.admin;

import com.knucse.diy.domain.model.student.Role;
import com.knucse.diy.domain.persistence.admin.AdminRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminAccountInitializer {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${admin.username}")
    private String username;

    @Value("${admin.password}")
    private String password;

    @PostConstruct
    public void init() {

        System.out.println(username);
        System.out.println(password);

        if (!adminRepository.existsByUsername(username)) {
            Admin admin = new Admin();

            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ROLE_DIY_MANAGER);

            adminRepository.save(admin);


        }
    }
}
