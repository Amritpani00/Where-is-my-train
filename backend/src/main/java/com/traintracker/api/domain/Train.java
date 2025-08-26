package com.traintracker.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "trains")
public class Train {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 10)
	private String number;

	@Column(nullable = false, length = 120)
	private String name;

	@Column(nullable = false)
	private boolean active;

	@Column
	private LocalDate effectiveFrom;
}