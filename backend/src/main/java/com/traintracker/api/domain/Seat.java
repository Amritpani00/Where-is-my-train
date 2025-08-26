package com.traintracker.api.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "seats", uniqueConstraints = {@UniqueConstraint(columnNames = {"coach_id", "seat_number"})})
public class Seat {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "coach_id")
	private Coach coach;

	@Column(name = "seat_number", nullable = false, length = 10)
	private String seatNumber; // e.g., 1, 2, 3 or 1A, 1B

	@Column(nullable = false, length = 10)
	private String travelClass; // SL, 3A, 2A, CC
}