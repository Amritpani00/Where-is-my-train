package com.traintracker.api.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.traintracker.api.domain.Booking;
import com.traintracker.api.domain.Payment;
import com.traintracker.api.repo.BookingRepository;
import com.traintracker.api.repo.PaymentRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class PaymentService {
	private final RazorpayClient razorpayClient;
	private final BookingRepository bookingRepository;
	private final PaymentRepository paymentRepository;

	@Value("${razorpay.keySecret:}")
	private String keySecret;

	@Value("${razorpay.webhookSecret:}")
	private String webhookSecret;

	public PaymentService(RazorpayClient razorpayClient, BookingRepository bookingRepository, PaymentRepository paymentRepository) {
		this.razorpayClient = razorpayClient;
		this.bookingRepository = bookingRepository;
		this.paymentRepository = paymentRepository;
	}

	@Transactional
	public Order createOrderForBooking(Long bookingId) throws Exception {
		Booking booking = bookingRepository.findById(bookingId).orElseThrow();
		BigDecimal amount = booking.getTotalAmount();
		JSONObject options = new JSONObject();
		options.put("amount", amount.multiply(BigDecimal.valueOf(100)).longValue());
		options.put("currency", "INR");
		options.put("receipt", "rcpt_" + booking.getPnr());
		options.put("payment_capture", 1);
		Order order = razorpayClient.orders.create(options);
		Payment payment = Payment.builder()
				.booking(booking)
				.providerOrderId(order.get("id"))
				.status("CREATED")
				.amount(amount)
				.currency("INR")
				.createdAt(OffsetDateTime.now())
				.build();
		paymentRepository.save(payment);
		return order;
	}

	@Transactional
	public boolean verifyAndCapture(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
		if (keySecret == null || keySecret.isBlank()) return false;
		String payload = razorpayOrderId + '|' + razorpayPaymentId;
		String computed = hmacSha256Hex(payload, keySecret);
		boolean ok = computed.equals(razorpaySignature);
		if (!ok) return false;
		Optional<Payment> latest = paymentRepository.findTopByProviderOrderIdOrderByIdDesc(razorpayOrderId);
		latest.ifPresent(p -> {
			p.setProviderPaymentId(razorpayPaymentId);
			p.setProviderSignature(razorpaySignature);
			p.setStatus("SUCCESS");
			paymentRepository.save(p);
		});
		return true;
	}

	public boolean verifyWebhook(String body, String signature) {
		if (webhookSecret == null || webhookSecret.isBlank()) return false;
		String computed = hmacSha256Hex(body, webhookSecret);
		return computed.equals(signature);
	}

	private static String hmacSha256Hex(String data, String secret) {
		try {
			javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
			mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
			byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
			StringBuilder sb = new StringBuilder();
			for (byte b : rawHmac) sb.append(String.format("%02x", b));
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}