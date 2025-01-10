package com.knucse.diy.domain.persistence.key;

import com.knucse.diy.domain.model.key.RoomKeyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomKeyHistoryRepository extends JpaRepository<RoomKeyHistory, Long> {
    // 특정 학생의 열쇠 이력 조회
    List<RoomKeyHistory> findByStudentName(String studentName);

    // 특정 열쇠 상태로 변경된 기록 조회
    List<RoomKeyHistory> findByStatus(String status);

    // 특정 날짜 이후의 기록 조회
    List<RoomKeyHistory> findByDateAfter(LocalDateTime date);
}
