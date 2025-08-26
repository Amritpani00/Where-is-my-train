package com.traintracker.api.repo;

import com.traintracker.api.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	Optional<Booking> findByPnr(String pnr);
	List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
	List<Booking> findByTravelDateBetween(LocalDate start, LocalDate end);
}