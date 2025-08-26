package com.traintracker.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 12)
	private String pnr;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private UserAccount user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "train_id", nullable = false)
	private Train train;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_station_id", nullable = false)
	private Station fromStation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_station_id", nullable = false)
	private Station toStation;

	@Column(nullable = false)
	private LocalDate travelDate;

	@Column(nullable = false, length = 20)
	private String status; // CREATED, CONFIRMED, CANCELLED

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal totalAmount;

	@Column(nullable = false)
	private OffsetDateTime createdAt;

	@OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<BookedSeat> bookedSeats = new ArrayList<>();
}