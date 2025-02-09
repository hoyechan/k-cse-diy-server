package com.knucse.diy.domain.service.student;

import java.util.List;

import com.knucse.diy.domain.exception.student.StudentNotFoundException;
import com.knucse.diy.domain.model.student.Student;
import com.knucse.diy.domain.persistence.student.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {
    private final StudentRepository studentRepository;

    /**
     * 학생 이름과 학번을 통해 학생을 조회합니다.
     *
     * @param studentName 학생 이름
     * @param studentNumber 학생 학번
     * @return 해당 이름과 학번을 가진 학생 엔티티
     * @throws StudentNotFoundException "STUDENT_NOT_FOUND"
     */
    public Student findStudentByNameAndNumber(String studentName, String studentNumber) {
        return studentRepository.findByStudentNameAndStudentNumber(studentName, studentNumber)
                .orElseThrow(StudentNotFoundException::new);
    }

    /**
     * 학번을 통해 학생을 조회합니다.
     *
     * @param studentNumber 학생 학번
     * @return 해당 학번을 가진 학생 엔티티
     * @throws StudentNotFoundException "STUDENT_NOT_FOUND"
     */
    public Student findStudentByStudentNumber(String studentNumber) {
        return studentRepository.findByStudentNumber(studentNumber)
                .orElseThrow(StudentNotFoundException::new);
    }

    /**
     * 이름을 통해 학생을 조회합니다.
     *
     * @param studentName 학생 이름
     * @return 해당 이름을 가진 학생 엔티티
     * @throws StudentNotFoundException "STUDENT_NOT_FOUND"
     */
    public Student findStudentByStudentName(String studentName) {
        return studentRepository.findByStudentName(studentName)
                .orElseThrow(StudentNotFoundException::new);
    }

    public List<Student> findAllStudents() {
        return studentRepository.findAll();
    }
}