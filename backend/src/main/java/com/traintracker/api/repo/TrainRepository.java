package com.traintracker.api.repo;

import com.traintracker.api.domain.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
	Optional<Train> findByNumber(String number);
	List<Train> findByActiveTrue();
}