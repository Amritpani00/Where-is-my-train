package com.traintracker.api.repo;

import com.traintracker.api.domain.BookedSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookedSeatRepository extends JpaRepository<BookedSeat, Long> {
	List<BookedSeat> findByBookingId(Long bookingId);
}