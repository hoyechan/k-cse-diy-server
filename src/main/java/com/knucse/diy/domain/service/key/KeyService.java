package com.knucse.diy.domain.service.key;

import com.knucse.diy.api.key.dto.*;
import com.knucse.diy.api.reservation.dto.ReservationReadDto;
import com.knucse.diy.domain.exception.key.KeyDuplicatedException;
import com.knucse.diy.domain.exception.key.KeyNotFoundException;
import com.knucse.diy.domain.exception.key.KeyRentAuthenticationFailedException;
import com.knucse.diy.domain.exception.key.KeyReturnAuthenticationFailedException;
import com.knucse.diy.domain.model.key.Key;
import com.knucse.diy.domain.model.key.KeyStatus;
import com.knucse.diy.domain.model.reservation.Reservation;
import com.knucse.diy.domain.model.student.Student;
import com.knucse.diy.domain.persistence.key.KeyRepository;
import com.knucse.diy.domain.persistence.reservation.ReservationRepository;
import com.knucse.diy.domain.service.reservation.ReservationService;
import com.knucse.diy.domain.service.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KeyService {

    private final KeyRepository keyRepository;
    private final ReservationService reservationService;
    private final StudentService studentService;

//    /**
//     * KeyCreateDto를 기반으로 Key를 생성합니다.
//     * @param keyCreateDto
//     * @return 생성된 key의 ReadDto
//     */
//    @Transactional(isolation = Isolation.REPEATABLE_READ)
//    public KeyReadDto createKey(KeyCreateDto keyCreateDto){
//        Key key = keyRepository.save(keyCreateDto.toEntity());
//        return KeyReadDto.fromEntity(key,null);
//    }

    /**
     * 모든 Key를 조회합니다.
     * @return 조회된 모든 Key 혹은 empty List
     */
    private List<Key> findAllKey(){
        return keyRepository.findAll();
    }

    /**
     * KeyId를 기반으로 Key를 조회합니다.
     * @param id Long
     * @return 조회된 Key
     * @throws KeyNotFoundException "KEY_NOT_FOUND"
     */
    public Key findKeyById(Long id){
        return keyRepository.findById(id)
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
     */
    @Transactional
    public String rentKey(KeyRentDto keyRentDto){
        Key key = findKeyById(1L); //우선 key id는 1L로 고정

        Student holder = studentService.findStudentByNameAndNumber(keyRentDto.StudentName(), keyRentDto.StudentNumber());

        //열쇠가 반납된 상태가 아닌데, 대여 누를 시 예외처리
        if(key.getStatus() != KeyStatus.KEEPING){
            throw new KeyRentAuthenticationFailedException();
        }

        Reservation reservationByStudentAndDate = reservationService.findReservationsByStudentAndDate(holder, LocalDate.now());

        //입력한 학생의 예약이 당일 없다면 예외처리
        if(reservationByStudentAndDate == null)
        {
            throw new KeyRentAuthenticationFailedException();
        }

        // 예약시간 30분 전부터 예약 종료 시간까지 열쇠 대여 가능
        LocalDateTime reservationStartTime = LocalDateTime.of(
                reservationByStudentAndDate.getReservationDate(),
                reservationByStudentAndDate.getStartTime()
        );

        LocalDateTime reservationEndTime = LocalDateTime.of(
                reservationByStudentAndDate.getReservationDate(),
                reservationByStudentAndDate.getEndTime()
        );

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(reservationStartTime.minusMinutes(30)) && now.isBefore(reservationEndTime)) {
            key.updateKey(holder, key.getLastUser(), KeyStatus.USING, key.getReturnedDateTime(), now);

            return "1234"; // 사물함 비밀번호 return
        } else {
            throw new KeyRentAuthenticationFailedException();
        }
    }

    /**
     * 열쇠를 반납하며 상태를 변경합니다.
     * @param keyReturnDto
     */
    public void returnKey(KeyReturnDto keyReturnDto){
        Student lastUser = studentService.findStudentByNameAndNumber(keyReturnDto.StudentName(), keyReturnDto.StudentNumber());

        Key key = findKeyById(1L);

        //열쇠의 상태가 사용중이 아닌데, 반납을 누를 시 예외처리
        if(key.getStatus() != KeyStatus.USING){
            throw new KeyRentAuthenticationFailedException();
        }

        Reservation reservationByStudentAndDate = reservationService.findReservationsByStudentAndDate(lastUser, LocalDate.now());

        //입력한 학생의 예약이 당일 없다면 예외처리
        if(reservationByStudentAndDate == null)
        {
            throw new KeyRentAuthenticationFailedException();
        }

        // 예약시간 30분 전부터 예약 종료 시간까지 열쇠 대여 가능
        LocalDateTime reservationStartTime = LocalDateTime.of(
                reservationByStudentAndDate.getReservationDate(),
                reservationByStudentAndDate.getStartTime()
        );

        LocalDateTime reservationEndTime = LocalDateTime.of(
                reservationByStudentAndDate.getReservationDate(),
                reservationByStudentAndDate.getEndTime()
        );

        LocalDateTime now = LocalDateTime.now();

        if (now.isAfter(reservationStartTime) && now.isBefore(reservationEndTime.plusMinutes(30))) {
            key.updateKey(null, lastUser, KeyStatus.KEEPING, LocalDateTime.now(), null);
        } else {
            throw new KeyReturnAuthenticationFailedException();
        }
    }




    /**
     * keyId를 기반으로 key를 삭제합니다.
     * @param keyId Long
     * @throws KeyNotFoundException "KEY_NOT_FOUND"
     */
    @Transactional
    public void deleteReservation(Long keyId){
        Key key = keyRepository.findById(keyId)
                .orElseThrow(KeyNotFoundException::new);

        keyRepository.delete(key);
    }

}
