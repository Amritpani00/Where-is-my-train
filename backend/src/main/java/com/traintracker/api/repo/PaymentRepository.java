package com.traintracker.api.repo;

import com.traintracker.api.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
	List<Payment> findByBookingId(Long bookingId);
	Optional<Payment> findTopByBookingIdOrderByIdDesc(Long bookingId);
	Optional<Payment> findTopByProviderOrderIdOrderByIdDesc(String providerOrderId);
}