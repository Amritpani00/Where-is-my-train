package com.traintracker.api.repo;

import com.traintracker.api.domain.Coach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachRepository extends JpaRepository<Coach, Long> {
	List<Coach> findByTrainIdOrderByCoachIndexAsc(Long trainId);
}