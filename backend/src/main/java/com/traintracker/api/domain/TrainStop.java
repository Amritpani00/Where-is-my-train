package com.traintracker.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "train_stops")
public class TrainStop {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "train_id")
	private Train train;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "station_id")
	private Station station;

	@Column(nullable = false)
	private Integer sequenceNumber;

	@Column
	private LocalTime arrivalTime;

	@Column
	private LocalTime departureTime;

	@Column(nullable = false)
	private Integer dayOffset;
}