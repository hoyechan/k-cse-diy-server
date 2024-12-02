package com.knucse.diy.domain.scheduler;

import com.knucse.diy.domain.model.key.RoomKey;
import com.knucse.diy.domain.model.key.RoomKeyStatus;
import com.knucse.diy.domain.model.reservation.Reservation;
import com.knucse.diy.domain.model.reservation.ReservationBlackList;
import com.knucse.diy.domain.model.student.Student;
import com.knucse.diy.domain.persistence.reservation.ReservationBlackListRepository;
import com.knucse.diy.domain.service.key.RoomKeyService;
import com.knucse.diy.domain.service.reservation.ReservationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RoomKeyScheduler {

    private final RoomKeyService roomKeyService;
    private final ReservationService reservationService;
    private final ReservationBlackListRepository reservationBlackListRepository;

    /**
     * 매 5분마다 Key 상태를 검사하여 미반납 상태 처리 및 Blacklist 등록
     */
    @Scheduled(cron = "0 */5 * * * *") // 5분마다 실행
    @Transactional
    public void checkKeyReturnStatus() {
        RoomKey roomKey = roomKeyService.findFirstKey();

        if(roomKey.getStatus() == RoomKeyStatus.USING){
            Reservation reservation = reservationService.findReservationsByStudentAndDate(roomKey.getHolder(), LocalDate.now());

            LocalDateTime reservationEndTime = LocalDateTime.of(LocalDate.now(), reservation.getEndTime());
            if (reservationEndTime.plusMinutes(30).isBefore(LocalDateTime.now())) {
                Student notReturendStudent = roomKey.getHolder();
                addToBlackList(notReturendStudent);

                roomKeyService.updateRoomKey(null, RoomKeyStatus.NOT_RETURNED);

                // 로그 출력 또는 추가 작업
                System.out.println("Key ID " + roomKey.getId() + " 미반납 상태로 변경, 학생 ID: " + notReturendStudent.getId() + " Blacklist 등록");
            }
        }
    }

    /**
     * Blacklist에 학생 추가
     */
    private void addToBlackList(Student student) {
        ReservationBlackList blackListedStudent = ReservationBlackList.builder()
                .student(student).build();

        reservationBlackListRepository.save(blackListedStudent);
    }
}
