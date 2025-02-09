package com.knucse.diy.domain.service.key;

import com.knucse.diy.api.key.dto.KeyCreateDto;
import com.knucse.diy.api.key.dto.KeyHistoryReadDto;
import com.knucse.diy.api.key.dto.KeyReadDto;
import com.knucse.diy.api.reservation.dto.ReservationReadDto;
import com.knucse.diy.domain.model.key.RoomKey;
import com.knucse.diy.domain.model.key.RoomKeyHistory;
import com.knucse.diy.domain.model.key.RoomKeyStatus;
import com.knucse.diy.domain.model.student.Student;
import com.knucse.diy.domain.persistence.key.RoomKeyHistoryRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomKeyHistoryService {
    private final RoomKeyHistoryRepository roomKeyHistoryRepository;

    /**
     * RoomKeyHistory를 생성합니다.
     * @return 생성된 RoomkeyHistory의 ReadDto
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public KeyHistoryReadDto createRoomKeyHistory(Student student, RoomKeyStatus status) {

        RoomKeyHistory history = RoomKeyHistory.builder()
                .student(student)
                .date(LocalDateTime.now())
                .status(status)
                .build();

        roomKeyHistoryRepository.save(history);

        return KeyHistoryReadDto.fromEntity(history);
    }

    /**
     * 모든 RoomKeyHistory를 조회합니다.
     * @return 조회된 모든 Key 혹은 empty List
     */
    public List<KeyHistoryReadDto> findAllRoomKeyHistory(){

        List<RoomKeyHistory> roomKeyHistories = roomKeyHistoryRepository.findAll();

        return roomKeyHistories.stream()
                .map(KeyHistoryReadDto::fromEntity)
                .collect(Collectors.toList());
    }
}
