package com.knucse.diy.domain.service.reservation;

import com.knucse.diy.api.reservation.dto.*;
import com.knucse.diy.domain.exception.authcode.AuthCodeBadRequestException;
import com.knucse.diy.domain.exception.authcode.AuthCodeMismatchException;
import com.knucse.diy.domain.exception.reservation.ReservationDuplicatedException;
import com.knucse.diy.domain.exception.reservation.ReservationNotFoundException;
import com.knucse.diy.domain.exception.student.StudentNotFoundException;
import com.knucse.diy.domain.model.reservation.Reservation;
import com.knucse.diy.domain.model.reservation.ReservationStatus;
import com.knucse.diy.domain.model.student.Role;
import com.knucse.diy.domain.model.student.Student;
import com.knucse.diy.domain.persistence.reservation.ReservationRepository;
import com.knucse.diy.domain.persistence.student.StudentRepository;
import com.knucse.diy.domain.service.student.StudentService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@SpringBootTest
@Transactional
class ReservationServiceIntegrationTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void makeStudent() {
        studentRepository.save(new Student("12345","John Doe",Role.ROLE_STUDENT));
    }

    @Test
    void createReservation_success() {
        // given
        ReservationCreateDto createDto = new ReservationCreateDto(
                "John Doe", "12345", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "산사랑 연극 연습", "1234"
        );

        // when
        ReservationReadDto reservationReadDto = reservationService.createReservation(createDto);

        // then
        assertNotNull(reservationReadDto);
        assertNotNull(reservationReadDto.id()); // ID가 자동 생성되었는지 확인
        assertEquals("John Doe", reservationReadDto.studentName());
        assertEquals("12345", reservationReadDto.studentNumber());
        assertEquals(LocalDate.now(), reservationReadDto.reservationDate());
        assertEquals(LocalTime.of(10, 0), reservationReadDto.startTime());
        assertEquals(LocalTime.of(12, 0), reservationReadDto.endTime());

        // 데이터베이스에서 실제로 저장된 예약을 확인
        Reservation savedReservation = reservationRepository.findById(reservationReadDto.id())
                .orElseThrow(() -> new AssertionError("Reservation not found"));
        assertEquals("John Doe", savedReservation.getStudent().getStudentName());
        assertEquals("12345", savedReservation.getStudent().getStudentNumber());
        assertEquals(LocalDate.now(), savedReservation.getReservationDate());
    }



    @Test
    void createReservation_duplicateReservation_throwsException() {
        // given
        ReservationCreateDto createDto1 = new ReservationCreateDto(
                "John Doe", "12345", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "산사랑 연극 연습", "1234"
        );

        ReservationCreateDto createDto2 = new ReservationCreateDto(
                "John Doe", "12345", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(11, 0), "산사랑 연극 연습2", "1233"
        );

        ReservationReadDto reservationReadDto1 = reservationService.createReservation(createDto1);


        // Act & Assert
        assertThrows(ReservationDuplicatedException.class,
                () -> reservationService.createReservation(createDto2));
    }

    @Test
    void createReservation_invalidAuthCode_throwsException() {
        // Arrange
        ReservationCreateDto createDto = new ReservationCreateDto(
                "John Doe", "12345", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "산사랑 연극 연습", "12"
        );

        // Act & Assert
        assertThrows(AuthCodeBadRequestException.class,
                () -> reservationService.createReservation(createDto));
    }

    @Test
    void findReservationById_notFound_throwsException() {
        // Arrang

        // Act & Assert
        assertThrows(ReservationNotFoundException.class,
                () -> reservationService.findReservationById(1L));
    }

    @Test
    void verifyAuthCode_success() {
        // Arrange
        ReservationCreateDto createDto = new ReservationCreateDto(
                "John Doe", "12345", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "산사랑 연극 연습", "1234"
        );

        ReservationReadDto reservationReadDto = reservationService.createReservation(createDto);

        reservationService.verifyAuthCode(reservationReadDto.id(), "1234");
    }

    @Test
    void verifyAuthCode_mismatch_throwsException() {

        ReservationCreateDto createDto = new ReservationCreateDto(
                "John Doe", "12345", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "산사랑 연극 연습", "1234"
        );

        ReservationReadDto reservationReadDto = reservationService.createReservation(createDto);

        // Act & Assert
        assertThrows(AuthCodeMismatchException.class,
                () -> reservationService.verifyAuthCode(reservationReadDto.id(), "1233"));
    }

    @Test
    void findReservationsByMonth_success() {
        // Arrange
        ReservationCreateDto createDto1 = new ReservationCreateDto(
                "John Doe", "12345", LocalDate.of(2024,11,1),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "산사랑 연극 연습", "1234"
        );

        ReservationCreateDto createDto2 = new ReservationCreateDto(
                "John Doe", "12345", LocalDate.of(2024,11,3),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "산사랑 연극 연습", "1234"
        );

        reservationService.createReservation(createDto1);
        reservationService.createReservation(createDto2);

        // Act
        List<ReservationReadDto> result = reservationService.findReservationsByMonth(YearMonth.of(2024,11));

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void findReservationsByStudent_success() {
        // Arrange
        ReservationCreateDto createDto1 = new ReservationCreateDto(
                "John Doe", "12345", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "산사랑 연극 연습", "1234"
        );
        ReservationCreateDto createDto2 = new ReservationCreateDto(
                "John Doe", "12345", LocalDate.now(),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "산사랑 연극 연습2", "1234"
        );

        reservationService.createReservation(createDto1);
        reservationService.createReservation(createDto2);

        // Act
        List<ReservationReadDto> result = reservationService.findReservationsByStudent("John Doe", "12345");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.reason().equals("산사랑 연극 연습")));
        assertTrue(result.stream().anyMatch(r -> r.reason().equals("산사랑 연극 연습2")));
    }

    @Test
    void updateReservationStatus_success() {
        // Arrange
        ReservationCreateDto createDto = new ReservationCreateDto(
                "John Doe", "12345", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "산사랑 연극 연습", "1234"
        );

        ReservationReadDto createdReservation = reservationService.createReservation(createDto);
        ReservationStatusUpdateDto updateDto = new ReservationStatusUpdateDto(createdReservation.id(), ReservationStatus.APPROVED);

        // Act
        reservationService.updateReservationStatus(updateDto);

        // Assert
        Reservation updatedReservation = reservationRepository.findById(createdReservation.id())
                .orElseThrow(() -> new AssertionError("Reservation not found"));
        assertEquals(ReservationStatus.APPROVED, updatedReservation.getStatus());
    }

    @Test
    void updateReservation_success() {
        // Arrange
        ReservationCreateDto createDto = new ReservationCreateDto(
                "John Doe", "12345", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "산사랑 연극 연습", "1234"
        );

        ReservationReadDto createdReservation = reservationService.createReservation(createDto);
        ReservationUpdateDto updateDto = new ReservationUpdateDto(
                createdReservation.id(), LocalTime.of(10,10),
                LocalTime.of(12, 0), "수정된 산사랑 연극 연습"
        );


        // Act
        reservationService.updateReservation(updateDto);

        // Assert
        Reservation updatedReservation = reservationRepository.findById(createdReservation.id())
                .orElseThrow(() -> new AssertionError("Reservation not found"));
        assertEquals(LocalTime.of(10, 10), updatedReservation.getStartTime());
        assertEquals(LocalTime.of(12, 0), updatedReservation.getEndTime());
        assertEquals("수정된 산사랑 연극 연습", updatedReservation.getReason());
    }

    @Test
    void deleteReservation_success() {
        // Arrange
        ReservationCreateDto createDto = new ReservationCreateDto(
                "John Doe", "12345", LocalDate.now(),
                LocalTime.of(10, 0), LocalTime.of(12, 0), "산사랑 연극 연습", "1234"
        );

        ReservationReadDto createdReservation = reservationService.createReservation(createDto);
        ReservationDeleteDto deleteDto = new ReservationDeleteDto(createdReservation.id(), "1234");

        // Act
        reservationService.deleteReservation(deleteDto);

        // Assert
        assertThrows(ReservationNotFoundException.class,
                () -> reservationService.findReservationById(createdReservation.id()));
    }

}
