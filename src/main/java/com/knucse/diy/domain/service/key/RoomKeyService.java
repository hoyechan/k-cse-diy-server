package com.knucse.diy.domain.service.key;

import com.knucse.diy.api.key.dto.*;
import com.knucse.diy.domain.exception.key.KeyNotFoundException;
import com.knucse.diy.domain.exception.key.KeyRentAuthenticationFailedException;
import com.knucse.diy.domain.exception.key.KeyReturnAuthenticationFailedException;
import com.knucse.diy.domain.exception.key.KeyStatusMissException;
import com.knucse.diy.domain.model.key.RoomKey;
import com.knucse.diy.domain.model.key.RoomKeyHistory;
import com.knucse.diy.domain.model.key.RoomKeyStatus;
import com.knucse.diy.domain.model.reservation.Reservation;
import com.knucse.diy.domain.model.student.Student;
import com.knucse.diy.domain.persistence.key.RoomKeyHistoryRepository;
import com.knucse.diy.domain.persistence.key.RoomKeyRepository;
import com.knucse.diy.domain.service.reservation.ReservationService;
import com.knucse.diy.domain.service.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomKeyService {

    private final RoomKeyRepository roomKeyRepository;
    private final ReservationService reservationService;
    private final StudentService studentService;
    private final RoomKeyHistoryRepository roomKeyHistoryRepository;

    /**
     * KeyCreateDto를 기반으로 Key를 생성합니다.
     * @return 생성된 key의 ReadDto
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public KeyReadDto createKey(){
        KeyCreateDto keyCreateDto = new KeyCreateDto();
        RoomKey roomKey= roomKeyRepository.save(keyCreateDto.toEntity());
        return KeyReadDto.fromEntity(roomKey,roomKey.getHolder());
    }

    /**
     * 모든 Key를 조회합니다.
     * @return 조회된 모든 Key 혹은 empty List
     */
    private List<RoomKey> findAllKey(){
        return roomKeyRepository.findAll();
    }

    /**
     * 제일 처음 등록된 RoomKey 가져오기
     */
    public RoomKey findFirstKey() {
        return roomKeyRepository.findFirstKey()
                .orElseThrow(KeyNotFoundException::new);
    }

    /**
     * KeyId를 기반으로 Key를 조회합니다.
     * @param id Long
     * @return 조회된 Key
     * @throws KeyNotFoundException "KEY_NOT_FOUND"
     */
    public RoomKey findKeyById(Long id){
        return roomKeyRepository.findById(id)
                .orElseThrow(KeyNotFoundException::new);
    }
//
//    /**
//     * KeyStatusUpdateDto를 기반으로 KeyStaus를 수정합니다.
//     * @param keyStatusUpdateDto KeyStatusUpdateDto
//     * @throws KeyNotFoundException "KEY_NOT_FOUND"
//     */
//    @Transactional
//    private void updateKeyStatus(KeyStatusUpdateDto keyStatusUpdateDto){
//        Key key = findKeyById(keyStatusUpdateDto.keyId());
//        key.updateStatus(keyStatusUpdateDto.status());
//    }

    /**
     * keyRentDto를 기반으로 key의 대여가능 여부 확인 및 대여합니다.
     * @param keyRentDto KeyRentDto
     * @return 사물함의 비밀번호
     * @throws KeyRentAuthenticationFailedException "KEY_RENT_AUTHENTICATION_FAILED"
     * @throws com.knucse.diy.domain.exception.student.StudentNotFoundException "STUDENT_NOT_FOUND"
     */
    @Transactional
    public KeyReadDto rentKey(KeyRentDto keyRentDto){
        RoomKey roomKey = findFirstKey(); //첫번째로 저장된 key 가져옴

        Student holder = studentService.findStudentByNameAndNumber(keyRentDto.studentName(), keyRentDto.studentNumber());
        System.out.println("holder.getStudentName() = " + holder.getStudentName());
        //열쇠가 반납된 상태가 아닌데, 대여 누를 시 예외처리
        if(roomKey.getStatus() != RoomKeyStatus.KEEPING){
            throw new KeyStatusMissException();
        }

        Reservation reservationByStudentAndDate = reservationService.findReservationsByStudentAndDate(holder, LocalDate.now());

        //입력한 학생의 예약이 당일 없다면 예외처리
        if(reservationByStudentAndDate == null)
        {
            throw new KeyRentAuthenticationFailedException();
        }

//        // 예약시간 30분 전부터 예약 종료 시간까지 열쇠 대여 가능
//        LocalDateTime reservationStartTime = LocalDateTime.of(
//                reservationByStudentAndDate.getReservationDate(),
//                reservationByStudentAndDate.getStartTime()
//        );
//
//        LocalDateTime reservationEndTime = LocalDateTime.of(
//                reservationByStudentAndDate.getReservationDate(),
//                reservationByStudentAndDate.getEndTime()
//        );
//
//        LocalDateTime now = LocalDateTime.now();

        roomKey.updateRoomKey(holder,RoomKeyStatus.USING);

        RoomKeyHistory history = RoomKeyHistory.builder()
                    .student(holder)
                    .date(LocalDateTime.now())
                    .status(RoomKeyStatus.USING)
                    .build();

        roomKeyHistoryRepository.save(history);

        return KeyReadDto.fromEntity(roomKey,holder);
    }

    /**
     * keyReturnDto를 기반으로 key의 반납가능 여부 확인 및 반납합니다.
     * @param keyReturnDto KeyReturnDto
     * @throws KeyRentAuthenticationFailedException "KEY_RENT_AUTHENTICATION_FAILED"
     * @throws KeyNotFoundException "KET_NOT_FOUND"
     * @throws com.knucse.diy.domain.exception.student.StudentNotFoundException "STUDENT_NOT_FOUND"
     */
    @Transactional
    public KeyReadDto returnKey(KeyReturnDto keyReturnDto){
        Student lastUser = studentService.findStudentByNameAndNumber(keyReturnDto.studentName(), keyReturnDto.studentNumber());

        RoomKey roomKey = findFirstKey();

        //열쇠를 대여한 사람이 아니라면 예외처리
        if(roomKey.getHolder() != lastUser){
            throw new KeyReturnAuthenticationFailedException();
        }

        //열쇠의 상태가 사용중이 아닌데, 반납을 누를 시 예외처리
        if(roomKey.getStatus() != RoomKeyStatus.USING){
            throw new KeyStatusMissException();
        }

        Reservation reservationByStudentAndDate = reservationService.findReservationsByStudentAndDate(lastUser, LocalDate.now());

        //입력한 학생의 예약이 당일 없다면 예외처리
        if(reservationByStudentAndDate == null)
        {
            throw new KeyRentAuthenticationFailedException();
        }

//        // 예약시간 30분 전부터 예약 종료 시간까지 열쇠 대여 가능
//        LocalDateTime reservationStartTime = LocalDateTime.of(
//                reservationByStudentAndDate.getReservationDate(),
//                reservationByStudentAndDate.getStartTime()
//        );
//
//        LocalDateTime reservationEndTime = LocalDateTime.of(
//                reservationByStudentAndDate.getReservationDate(),
//                reservationByStudentAndDate.getEndTime()
//        );
//
//        LocalDateTime now = LocalDateTime.now();

        roomKey.updateRoomKey(lastUser,RoomKeyStatus.KEEPING);

        RoomKeyHistory history = RoomKeyHistory.builder()
                    .student(lastUser)
                    .date(LocalDateTime.now())
                    .status(RoomKeyStatus.KEEPING)
                    .build();

        roomKeyHistoryRepository.save(history);

        return KeyReadDto.fromEntity(roomKey,lastUser);
    }

    public void updateRoomKey(Student student, RoomKeyStatus roomKeyStatus){
        RoomKey key = findFirstKey();
        key.updateRoomKey(student, roomKeyStatus);
    }


    /**
     * keyId를 기반으로 key를 삭제합니다.
     * @param keyId Long
     * @throws KeyNotFoundException "KEY_NOT_FOUND"
     */
    @Transactional
    public void deleteRoomKey(Long keyId){
        RoomKey key = roomKeyRepository.findById(keyId)
                .orElseThrow(KeyNotFoundException::new);

        roomKeyRepository.delete(key);
    }

}
