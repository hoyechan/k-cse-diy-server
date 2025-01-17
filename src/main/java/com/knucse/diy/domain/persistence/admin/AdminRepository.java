package com.knucse.diy.domain.persistence.admin;

import com.knucse.diy.domain.model.admin.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Boolean existsByUsername(String username);

    Admin findByUsername(String username);
}
