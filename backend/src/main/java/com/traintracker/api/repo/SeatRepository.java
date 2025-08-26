package com.traintracker.api.repo;

import com.traintracker.api.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
	List<Seat> findByCoachId(Long coachId);
}