package com.knucse.diy.domain.service.reservation;

import com.knucse.diy.api.reservation.dto.*;
import com.knucse.diy.domain.exception.authcode.AuthCodeBadRequestException;
import com.knucse.diy.domain.exception.authcode.AuthCodeMismatchException;
import com.knucse.diy.domain.exception.reservation.ReservationDuplicatedException;
import com.knucse.diy.domain.exception.reservation.ReservationNotFoundException;
import com.knucse.diy.domain.model.reservation.Reservation;
import com.knucse.diy.domain.model.student.Student;
import com.knucse.diy.domain.persistence.reservation.ReservationRepository;
import com.knucse.diy.domain.service.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import com.knucse.diy.domain.exception.student.StudentNotFoundException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import static com.knucse.diy.common.util.datetime.DateTimeUtil.isBetweenInclusive;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final StudentService studentService;

    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * reservation을 생성합니다.
     * @param createDto ReservationCreateDto
     * @return 생성된 Reservation의 readDto
     * @throws StudentNotFoundException "STUDENT_NOT_FOUND"
     * @throws ReservationDuplicatedException "RESERVATION_DUPLICATED"
     * @throws AuthCodeBadRequestException "AUTHENTICATION_CODE_MUST_BE_4_DIGITS";
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    ReservationReadDto createReservation(ReservationCreateDto createDto)
    {
        Student student = retrieveStudent(createDto);

        List<Reservation> reservations = findReservationByDate(createDto.reservationDate());
        //예약 신청한 날짜,시간과 겹치는 예약이 있다면 예외처리
        if(!reservations.isEmpty()){
            for(Reservation reservation : reservations){
                if (isBetweenInclusive(createDto.startTime(),reservation.getStartTime(),reservation.getEndTime())
                ||  isBetweenInclusive(createDto.endTime(), reservation.getStartTime(), reservation.getEndTime())) {
                    throw new ReservationDuplicatedException();
                }
            }
        }

        String authCode = createDto.authCode();
        //인증번호 길이가 4자리 숫자가 아니라면 예외처리
        if (!authCode.matches("\\d{4}")) {
            throw new AuthCodeBadRequestException();
        }

        String hashedCode = passwordEncoder.encode(authCode); // 인증번호 해싱

        Reservation reservation = reservationRepository.save(createDto.toEntity(student,hashedCode));
        return ReservationReadDto.fromEntity(reservation);
    }

    /**
     * reservationId를 기반으로 reservation을 조회합니다.
     * @param reservationId Long
     * @return 조회된 reservation
     * @throws ReservationNotFoundException "RESERVATION_NOT_FOUND"
     */
    Reservation findReservationById(Long reservationId){
        return reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
    }


    /**
     * reservationDate(특정 일)를 기반으로 reservation을 조회합니다.
     * @param reservationDate LocalDate
     * @return 조회된 reservation entity List 혹은 empty List
     */
    private List<Reservation> findReservationByDate(LocalDate reservationDate){
        return reservationRepository.findByReservationDate(reservationDate);
    }

    /**
     * reservationMonth(특정 월)을 기반으로 reservation을 조회합니다.
     *
     * @param yearMonth YearMonth
     * @return 조회된 reservation entity List 혹은 empty List
     */
    public List<ReservationReadDto> findReservationsByMonth(YearMonth yearMonth) {
        LocalDate startOfMonth = yearMonth.atDay(1); // 해당 월의 첫 번째 날
        LocalDate endOfMonth = yearMonth.atEndOfMonth(); // 해당 월의 마지막 날

        List<Reservation> reservations = reservationRepository.findByReservationDateBetween(startOfMonth, endOfMonth);

        // Reservation -> ReservationReadDto 변환
        return reservations.stream()
                .map(ReservationReadDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ReservationCreateDto를 기반으로 student를 조회합니다.
     * @param createDto ReservationCreateDto
     * @return 조회된 student entity
     * @throws StudentNotFoundException "STUDENT_NOT_FOUND"
     */
    private Student retrieveStudent(ReservationCreateDto createDto) {
        return studentService.findStudentByNameAndNumber(
                createDto.studentName(),
                createDto.studentNumber()
        );
    }

    /**
     * 특정 학생의 이름과 학번을 기반으로 모든 예약을 조회합니다.
     *
     * @param studentName   String
     * @param studentNumber String
     * @return 해당 학생의 ReservationReadDto List 혹은 empty List
     * @throws StudentNotFoundException "STUDENT_NOT_FOUND"
     */
    public List<ReservationReadDto> findReservationsByStudent(String studentName, String studentNumber) {
        Student student = studentService.findStudentByNameAndNumber(studentName, studentNumber);

        List<Reservation> reservations = reservationRepository.findByStudent(student);

        // Reservation -> ReservationReadDto 변환
        return reservations.stream()
                .map(ReservationReadDto::fromEntity)
                .collect(Collectors.toList());
    }


    /**
     * 입력받은 authCode와 reservation의 authCode가 일치하는지 검사합니다.
     * @param reservationId Long
     * @param authCode String
     * @throws AuthCodeMismatchException "AUTH CODE MISMATCH"
     */
    public void verifyAuthCode(Long reservationId, String authCode){
        Reservation reservation = findReservationById(reservationId);

        if(!passwordEncoder.matches(authCode, reservation.getAuthCode())){
            throw new AuthCodeMismatchException();
        }
    }

    /**
     * ReservationStatusUpdateDto를 기반으로 reservationStatus를 수정합니다.
     * @param updateDto ReservationUpdateDto
     * @throws ReservationNotFoundException "RESERVATION_NOT_FOUND"
     */
    @Transactional
    public void updateReservationStatus(ReservationStatusUpdateDto updateDto) {
        Reservation reservation = findReservationById(updateDto.reservationId());

        reservation.updateStatus(updateDto.reservationStatus());
    }

    /**
     * ReservationUpdateDto를 기반으로 reservation을 수정합니다.
     * @param updateDto ReservationUpdateDto
     * @throws ReservationNotFoundException "RESERVATION_NOT_FOUND"
     */
    @Transactional
    public void updateReservation(ReservationUpdateDto updateDto){
        Reservation reservation = findReservationById(updateDto.ReservationId());

        reservation.updateReservation(updateDto);
    }

    /**
     * reservationDeleteDto를 기반으로 reservation을 삭제합니다.
     * @param reservationDeleteDto ReservationDeleteDto
     * @throws ReservationNotFoundException "RESERVATION_NOT_FOUND"
     * @throws AuthCodeMismatchException "AUTH CODE MISMATCH"
     */
    @Transactional
    public void deleteReservation(ReservationDeleteDto reservationDeleteDto){
        Reservation reservation = findReservationById(reservationDeleteDto.ReservationId());

        verifyAuthCode(reservationDeleteDto.ReservationId(), reservationDeleteDto.authCode());

        reservationRepository.delete(reservation);
    }

}
