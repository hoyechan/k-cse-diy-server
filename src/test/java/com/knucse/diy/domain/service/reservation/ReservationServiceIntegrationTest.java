package com.knucse.diy.domain.service.reservation;

import com.knucse.diy.api.key.dto.KeyRentDto;
import com.knucse.diy.api.key.dto.KeyReturnDto;
import com.knucse.diy.api.reservation.dto.*;
import com.knucse.diy.domain.exception.authcode.AuthCodeBadRequestException;
import com.knucse.diy.domain.exception.authcode.AuthCodeMismatchException;
import com.knucse.diy.domain.exception.reservation.ReservationDuplicatedException;
import com.knucse.diy.domain.exception.reservation.ReservationNotFoundException;
import com.knucse.diy.domain.exception.student.StudentNotFoundException;
import com.knucse.diy.domain.model.key.Key;
import com.knucse.diy.domain.model.key.KeyStatus;
import com.knucse.diy.domain.model.reservation.Reservation;
import com.knucse.diy.domain.model.reservation.ReservationStatus;
import com.knucse.diy.domain.model.student.Role;
import com.knucse.diy.domain.model.student.Student;
import com.knucse.diy.domain.persistence.key.KeyRepository;
import com.knucse.diy.domain.persistence.reservation.ReservationRepository;
import com.knucse.diy.domain.persistence.student.StudentRepository;
import com.knucse.diy.domain.service.key.KeyService;
import com.knucse.diy.domain.service.student.StudentService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
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

    @Autowired
    private KeyRepository keyRepository;

    @Autowired
    private KeyService keyService;

    @BeforeEach
    void makeStudent() {
        Student student = new Student("12345","John Doe",Role.ROLE_STUDENT);
        Student student2 = new Student("123456","호예찬",Role.ROLE_STUDENT);
        studentRepository.save(student);
        studentRepository.save(student2);
        keyRepository.save(new Key(null,null, KeyStatus.KEEPING, null, null));
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
//        ReservationCreateDto createDto1 = new ReservationCreateDto(
//                "John Doe", "12345", LocalDate.now(),
//                LocalTime.of(10, 0), LocalTime.of(12, 0), "지난 산사랑 연극 연습", "1234"
//        );
        ReservationCreateDto createDto2 = new ReservationCreateDto(
                "John Doe", "12345", LocalDate.now(),
                LocalTime.of(1, 0), LocalTime.of(23, 30), "예정된 산사랑 연극 연습2", "1234"
        );

        //reservationService.createReservation(createDto1);
        reservationService.createReservation(createDto2);

        // Act
        List<ReservationReadDto> result = reservationService.findUpcomingReservationByStudent("John Doe", "12345");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertTrue(result.stream().anyMatch(r -> r.reason().equals("예정된 산사랑 연극 연습2")));
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

        ReservationCreateDto createDto2 = new ReservationCreateDto(
                "호예찬", "123456", LocalDate.now(),
                LocalTime.of(12, 0), LocalTime.of(13, 0), "산사랑 연극 연습", "1234"
        );


        ReservationReadDto createdReservation = reservationService.createReservation(createDto);
        ReservationReadDto createdReservation2 = reservationService.createReservation(createDto2);

        ReservationUpdateDto updateDto = new ReservationUpdateDto(
                createdReservation2.id(), LocalTime.of(12,10),
                LocalTime.of(13, 0), "호예찬의 수정된 산사랑 연극 연습","1234"
        );


        // Act
        reservationService.updateReservation(updateDto);

        Reservation reservation = reservationService.findReservationById(updateDto.ReservationId());
        assertEquals(reservation.getReason(),"호예찬의 수정된 산사랑 연극 연습");
        assertEquals(reservation.getStartTime(),LocalTime.of(12,10));
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

    @Test
    void keyRentAndReturn_success(){
        ReservationCreateDto createDto = new ReservationCreateDto(
                "John Doe", "12345", LocalDate.now(),
                LocalTime.of(5,0 ), LocalTime.of(5, 50), "가까운 산사랑 연극 연습", "1234"
        );

        ReservationReadDto reservation = reservationService.createReservation(createDto);

        Student student = studentService.findStudentByNameAndNumber("John Doe", "12345");

        KeyRentDto keyRentDto = new KeyRentDto("John Doe", "12345");

        String s = keyService.rentKey(keyRentDto);

        Key key = keyService.findKeyById(1L);
        System.out.println("key.getStatus() = " + key.getStatus());
        System.out.println("key.getHolder() = " + key.getHolder());
        System.out.println("key.getLastUser() = " + key.getLastUser());
        System.out.println("key.getRentalDateTime() = " + key.getRentalDateTime());
        System.out.println("key.getReturnedDateTime() = " + key.getReturnedDateTime());
        System.out.println("s = " + s);

        KeyReturnDto returnDto = new KeyReturnDto("John Doe", "12345", "1234");
        keyService.returnKey(returnDto);
        System.out.println("----------------------------------------");
        System.out.println("key.getStatus() = " + key.getStatus());
        System.out.println("key.getHolder() = " + key.getHolder());
        System.out.println("key.getLastUser() = " + key.getLastUser());
        System.out.println("key.getRentalDateTime() = " + key.getRentalDateTime());
        System.out.println("key.getReturnedDateTime() = " + key.getReturnedDateTime());

    }



}
