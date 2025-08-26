package com.traintracker.api.repo;

import com.traintracker.api.domain.TrainStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainStopRepository extends JpaRepository<TrainStop, Long> {
	List<TrainStop> findByTrainIdOrderBySequenceNumberAsc(Long trainId);
	List<TrainStop> findByStation_CodeOrderByTrainIdAscSequenceNumberAsc(String stationCode);
}