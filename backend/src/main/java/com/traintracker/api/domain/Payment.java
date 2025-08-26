package com.traintracker.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "payments")
public class Payment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "booking_id", nullable = false)
	private Booking booking;

	@Column(length = 40)
	private String providerOrderId; // Razorpay order id

	@Column(length = 40)
	private String providerPaymentId; // Razorpay payment id

	@Column(length = 100)
	private String providerSignature; // signature

	@Column(nullable = false, length = 20)
	private String status; // CREATED, SUCCESS, FAILED

	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal amount;

	@Column(nullable = false, length = 10)
	private String currency;

	@Column(nullable = false)
	private OffsetDateTime createdAt;
}