package com.traintracker.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "coaches")
public class Coach {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "train_id")
	private Train train;

	@Column(nullable = false, length = 10)
	private String coachType; // e.g., SL, 3A, 2A, CC

	@Column(nullable = false)
	private Integer coachIndex; // position in formation
}