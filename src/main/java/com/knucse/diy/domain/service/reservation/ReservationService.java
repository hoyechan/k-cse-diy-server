package com.knucse.diy.domain.service.reservation;

import com.knucse.diy.api.reservation.dto.*;
import com.knucse.diy.domain.exception.authcode.AuthCodeBadRequestException;
import com.knucse.diy.domain.exception.authcode.AuthCodeMismatchException;
import com.knucse.diy.domain.exception.reservation.ReservationDateOutOfRangeException;
import com.knucse.diy.domain.exception.reservation.ReservationDuplicatedException;
import com.knucse.diy.domain.exception.reservation.ReservationNotFoundException;
import com.knucse.diy.domain.exception.reservation.ReservationDailyLimitReachedException;
import com.knucse.diy.domain.model.reservation.Reservation;
import com.knucse.diy.domain.model.reservation.ReservationStatus;
import com.knucse.diy.domain.model.student.Student;
import com.knucse.diy.domain.persistence.reservation.ReservationRepository;
import com.knucse.diy.domain.service.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import com.knucse.diy.domain.exception.student.StudentNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.knucse.diy.common.util.datetime.DateTimeUtil.isBetweenInclusive;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
     * @throws AuthCodeBadRequestException "AUTHENTICATION_CODE_MUST_BE_4_DIGITS"
     * @throws ReservationDailyLimitReachedException "DAILY_LIMIT_REACHED"
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ReservationReadDto createReservation(ReservationCreateDto createDto)
    {
        Student student = retrieveStudent(createDto);


        Reservation reservationByStudentAndDate = findReservationsByStudentAndDate(student, createDto.reservationDate());

        //현재 시간으로부터 4주 이내의 날짜만 예약할 수 있습니다
        if(!isBetweenInclusive(createDto.reservationDate(), LocalDate.now().minusDays(1), LocalDate.now().plusDays(29))){
            throw new ReservationDateOutOfRangeException();
        }

        //한 학생은 하루에 두번 예약할 수 없습니다
        if(reservationByStudentAndDate != null){
            throw new ReservationDailyLimitReachedException();
        }


        String authCode = createDto.authCode();
        //인증번호 길이가 4자리 숫자가 아니라면 예외처리
        if (!authCode.matches("\\d{4}")) {
            throw new AuthCodeBadRequestException();
        }

        String hashedCode = passwordEncoder.encode(authCode); // 인증번호 해싱

        Reservation reservation = createDto.toEntity(student, hashedCode);

        //겹치는 시간대의 예약이 있는지 확인
        if(isReservationTimeOverlapping(reservation)){
            throw new ReservationDuplicatedException();
        }else {
            Reservation savedReservation = reservationRepository.save(reservation);
            return ReservationReadDto.fromEntity(savedReservation);
        }
    }

    /**
     * reservationId를 기반으로 reservation을 조회합니다.
     * @param reservationId Long
     * @return 조회된 reservation
     * @throws ReservationNotFoundException "RESERVATION_NOT_FOUND"
     */
    public Reservation findReservationById(Long reservationId){
        return reservationRepository.findById(reservationId)
                .orElseThrow(ReservationNotFoundException::new);
    }


    /**
     * reservationDate(특정 일)를 기반으로 reservation을 조회합니다.
     * @param reservationDate LocalDate
     * @return 조회된 reservation entity List 혹은 empty List
     */
    public List<Reservation> findReservationByDate(LocalDate reservationDate){
        return reservationRepository.findByReservationDate(reservationDate);
    }

    /**
     * reservationDate와 학생을 기반으로 reservation을 조회합니다.
     * @param student Student
     * @param date LocalDate
     * @return 조회된 reservation entity 혹은 null
     */
    public Reservation findReservationsByStudentAndDate(Student student, LocalDate date) {
        return reservationRepository.findByStudentAndReservationDate(student, date)
                .orElse(null); // Optional을 처리하여 null 반환
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
     * 특정 학생의 이름과 학번을 기반으로, 앞으로 예정된 예약을 조회합니다.
     * @param studentName String
     * @param studentNumber String
     * @return 해당 학생의 ReservationReadDto List 혹은 empty List
     * @throws StudentNotFoundException "STUDENT_NOT_FOUND"
     */
    public List<ReservationReadDto> findUpcomingReservationByStudent(String studentName, String studentNumber){
        Student student = studentService.findStudentByNameAndNumber(studentName, studentNumber);

        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        List<Reservation> reservations = reservationRepository.findUpcomingReservations(student, nowDate, nowTime);

        return reservations.stream()
                .map(ReservationReadDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 현재 시점으로부터 가까운 예약을 최대 limit개 만큼 조회합니다.
     *
     * @param limit
     * @return 가져온 reservation의 readDto 혹은 empty list
     */
    public List<ReservationReadDto> getClosestReservations(int limit) {
        LocalDate currentDate = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        Pageable pageable = PageRequest.of(0, limit); // Maximum 'limit' reservations
        List<Reservation> reservations = reservationRepository.findClosestReservations(currentDate, currentTime, pageable);

        return reservations.stream()
                .map(ReservationReadDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * targetDate과 minusDay, plusDay로 정해진 범위의 예약을 가져옵니다.
     * @param targetDate LocalDate
     * @return 가져온 reservation의 ReadDtoList 혹은 빈 리스트
     */
    public List<ReservationReadDto> getReservationsWithinRange(LocalDate targetDate,long minusDay, long plusDay) {
        LocalDate startDate = targetDate.minusDays(minusDay);
        LocalDate endDate = targetDate.plusDays(plusDay);

        List<Reservation> reservations = reservationRepository.findReservationsWithinDateRange(startDate, endDate);

        // Reservation -> ReservationReadDto 변환
        return reservations.stream()
                .map(ReservationReadDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * startDate와 endDate 사이의 예약을 가져옵니다.
     * @param startDate LocalDate
     * @param endDate LocalDate
     * @return 가져온 reservation의 ReadDtoList 혹은 빈 리스트
     */
    public List<ReservationReadDto> getReservationsWithinRange(LocalDate startDate, LocalDate endDate) {
        List<Reservation> reservations = reservationRepository.findReservationsWithinDateRange(startDate, endDate);

        // Reservation -> ReservationReadDto 변환
        return reservations.stream()
                .map(ReservationReadDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 학생 이름을 기반으로 예약을 조회합니다.
     * @param studentName
     * @return 조회된 예약의 readDto
     */
    public List<ReservationReadDto> findReservationByStudentName(String studentName){
        Student student = studentService.findStudentByStudentName(studentName);

        List<Reservation> reservations = reservationRepository.findByStudent(student);

        // Reservation -> ReservationReadDto 변환
        return reservations.stream()
                .map(ReservationReadDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 학생 학번을 기반으로 예약을 조회합니다.
     * @param studentNumber
     * @return 조회된 예약의 readDto
     */
    public List<ReservationReadDto> findReservationByStudentNumber(String studentNumber){
        Student student = studentService.findStudentByStudentNumber(studentNumber);

        List<Reservation> reservations = reservationRepository.findByStudent(student);

        // Reservation -> ReservationReadDto 변환
        return reservations.stream()
                .map(ReservationReadDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 예약 상태를 기반으로 예약을 조회합니다.
     * @param status
     * @return 조회된 예약의 readDto
     */
    public List<ReservationReadDto> findReservationByStatus(ReservationStatus status){

        List<Reservation> reservations = reservationRepository.findByStatus(status);

        // Reservation -> ReservationReadDto 변환
        return reservations.stream()
                .map(ReservationReadDto::fromEntity)
                .collect(Collectors.toList());
    }


    /**
     * 입력받은 reservation을 기반으로 겹치는 시간대의 reservation이 있는지 검사
     * @param reservation Reservation
     * @return 겹치는 시간대의 reservation이 없다면 False, 있다면 true
     * @throws ReservationDuplicatedException "RESERVATION_DUPLICATED"
     */
    public boolean isReservationTimeOverlapping(Reservation reservation){
        List<Reservation> reservations = findReservationByDate(reservation.getReservationDate());

        //예약 신청한 날짜,시간과 겹치는 예약이 있다면 true 반환
        if(!reservations.isEmpty()){
            for(Reservation checkReservation : reservations){

                //예약 수정 시 발생하는 오류 수정
                if(checkReservation.getId().equals(reservation.getId())){
                    continue;
                }

                //거절 상태의 예약은 중복 처리 안함
                if(checkReservation.getStatus().equals(ReservationStatus.CANCELLED)){
                    continue;
                }

                if(reservation.getStartTime().equals(checkReservation.getStartTime()) &&
                        reservation.getEndTime().equals(checkReservation.getEndTime())){
                    return true;
                }


                if (isBetweenInclusive(reservation.getStartTime(),checkReservation.getStartTime(),checkReservation.getEndTime())
                        ||  isBetweenInclusive(reservation.getEndTime(), checkReservation.getStartTime(), checkReservation.getEndTime())) {
                    return true;
                }

                if(isBetweenInclusive(checkReservation.getStartTime(), reservation.getStartTime(), reservation.getEndTime())
                        || isBetweenInclusive(checkReservation.getEndTime(), reservation.getStartTime(), reservation.getEndTime())){
                    return true;
                }
            }
        }
        return false;
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
     * @return 바뀐 Reservation의 ReadDto
     */
    @Transactional
    public ReservationReadDto updateReservationStatus(ReservationStatusUpdateDto updateDto) {
        Reservation reservation = findReservationById(updateDto.reservationId());

        reservation.updateStatus(updateDto.reservationStatus());

        return ReservationReadDto.fromEntity(reservation);
    }

    /**
     * list형태로 받은 reservation들의 status를 승인 상태로 변경합니다.
     * @param ids List<Long>
     * @return 변경된 reservation의 readDtoList
     */
    @Transactional
    public List<ReservationReadDto> updateReservationListStatus(List<Long> ids) {

        List<Reservation> reservations = new ArrayList<>();

        for (Long id : ids) {
            Reservation reservation = findReservationById(id);
            reservation.updateStatus(ReservationStatus.APPROVED);
            reservations.add(reservation);
        }

        // Reservation -> ReservationReadDto 변환
        return reservations.stream()
                .map(ReservationReadDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * ReservationCancelDto를 기반으로 reservation을 수정합니다.
     * @param cancelDto ReservationCancelDto
     * @return 바뀐 Reservation의 ReadDto
     * @throws ReservationNotFoundException "RESERVATION_NOT_FOUND"
     */
    @Transactional
    public ReservationReadDto cancelReservation(ReservationCancelDto cancelDto) {
        Reservation reservation = findReservationById(cancelDto.reservationId());
        reservation.cancelReservation(ReservationStatus.CANCELLED, cancelDto.cancelledReason());

        return ReservationReadDto.fromEntity(reservation);
    }

    /**
     * ReservationAuthCodeUpdateDto를 기반으로 reservation을 수정합니다.
     * @param updateDto ReservationAuthCodeUpdateDto
     * @return 바뀐 Reservation의 ReadDto
     * @throws ReservationNotFoundException "RESERVATION_NOT_FOUND"
     */
    @Transactional
    public ReservationReadDto updateAuthCode(ReservationAuthCodeUpdateDto updateDto) {
        Reservation reservation = findReservationById(updateDto.reservationId());

        String authCode = updateDto.newAuthCode();
        //인증번호 길이가 4자리 숫자가 아니라면 예외처리
        if (!authCode.matches("\\d{4}")) {
            throw new AuthCodeBadRequestException();
        }

        String hashedCode = passwordEncoder.encode(authCode); // 인증번호 해싱

        reservation.updateAuthCode(hashedCode);

        return ReservationReadDto.fromEntity(reservation);
    }




    /**
     * ReservationUpdateDto를 기반으로 reservation을 수정합니다.
     *
     * @param updateDto ReservationUpdateDto
     * @throws ReservationNotFoundException   "RESERVATION_NOT_FOUND"
     * @throws AuthCodeMismatchException      "AUTH CODE MISMATCH"
     * @throws ReservationDuplicatedException "RESERVATION_DUPLICATED"
     */
    @Transactional
    public ReservationReadDto updateReservation(ReservationUpdateDto updateDto){
        Reservation reservation = findReservationById(updateDto.reservationId());
        verifyAuthCode(updateDto.reservationId(), updateDto.authCode());

        reservation.updateReservation(updateDto);

        //겹치는 시간이 있다면 예외 처리
        if(isReservationTimeOverlapping(reservation)){
            System.out.println("중복 처리 있음");
            throw new ReservationDuplicatedException();
        }

        return ReservationReadDto.fromEntity(reservation);
    }

    /**
     * reservationDeleteDto를 기반으로 reservation을 삭제합니다.
     * @param reservationDeleteDto ReservationDeleteDto
     * @throws ReservationNotFoundException "RESERVATION_NOT_FOUND"
     * @throws AuthCodeMismatchException "AUTH CODE MISMATCH"
     */
    @Transactional
    public void deleteReservation(ReservationDeleteDto reservationDeleteDto){
        Reservation reservation = findReservationById(reservationDeleteDto.reservationId());

        verifyAuthCode(reservationDeleteDto.reservationId(), reservationDeleteDto.authCode());

        reservationRepository.delete(reservation);
    }

    @Transactional
    public void deleteReservation(Long reservationId){
        Reservation reservation = findReservationById(reservationId);

        reservationRepository.delete(reservation);
    }

}
