package com.knucse.diy.domain.persistence.student;

import java.util.Optional;

import com.knucse.diy.domain.model.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
	Optional<Student> findStudentById(Long id);
	Optional<Student> findByStudentNameAndStudentNumber(String studentName, String studentNumber);
	Optional<Student> findByStudentNumber(String studentNumber);
	Optional<Student> findByStudentName(String studentName);
}
