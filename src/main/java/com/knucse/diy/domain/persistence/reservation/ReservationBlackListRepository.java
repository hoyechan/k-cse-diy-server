package com.knucse.diy.domain.persistence.reservation;

import com.knucse.diy.domain.model.reservation.ReservationBlackList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationBlackListRepository extends JpaRepository<ReservationBlackList, Long> {

    /**
     * 특정 날짜 이전에 블랙리스트에 추가된 항목 조회
     */
    List<ReservationBlackList> findAllByCreatedAtBefore(LocalDateTime date);
}
