package com.knucse.diy.domain.persistence.reservation;

import com.knucse.diy.domain.model.reservation.Reservation;
import com.knucse.diy.domain.model.reservation.ReservationStatus;
import com.knucse.diy.domain.model.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    @Override
    Optional<Reservation> findById(Long id);

    List<Reservation> findByReservationDate(LocalDate reservationDate);

    Optional<Reservation> findByStudentAndReservationDate(Student student, LocalDate reservationDate);

    List<Reservation> findByReservationDateBetween(LocalDate startDate, LocalDate endDate);
    List<Reservation> findByStudent(Student student);
    List<Reservation> findByStatus(ReservationStatus status);

    @Query("SELECT r FROM Reservation r " +
            "WHERE r.student = :student " +
            "AND (r.reservationDate > :nowDate OR " +
            "(r.reservationDate = :nowDate AND r.startTime > :nowTime))")
    List<Reservation> findUpcomingReservations(
            @Param("student") Student student,
            @Param("nowDate") LocalDate nowDate,
            @Param("nowTime") LocalTime nowTime);



    @Query("""
            SELECT r 
            FROM Reservation r 
            WHERE (r.reservationDate > :currentDate) 
               OR (r.reservationDate = :currentDate AND r.startTime >= :currentTime)
            ORDER BY r.reservationDate ASC, r.startTime ASC
            """)
    List<Reservation> findClosestReservations(@Param("currentDate") LocalDate currentDate,
                                              @Param("currentTime") LocalTime currentTime,
                                              Pageable pageable);

    @Query("SELECT r FROM Reservation r WHERE r.reservationDate BETWEEN :startDate AND :endDate")
    List<Reservation> findReservationsWithinDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}

