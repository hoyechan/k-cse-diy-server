package com.knucse.diy.domain.persistence.reservation;

import com.knucse.diy.domain.model.reservation.Reservation;
import com.knucse.diy.domain.model.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {
    @Override
    Optional<Reservation> findById(Long id);

    List<Reservation> findByReservationDate(LocalDate reservationDate);

    List<Reservation> findByReservationDateBetween(LocalDate startDate, LocalDate endDate);
    List<Reservation> findByStudent(Student student);
}

