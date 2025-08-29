package com.traintracker.api.web;

import com.traintracker.api.domain.Booking;
import com.traintracker.api.domain.Payment;
import com.traintracker.api.repo.BookingRepository;
import com.traintracker.api.repo.PaymentRepository;
import com.traintracker.api.service.EmailService;
import com.traintracker.api.service.EventBus;
import com.traintracker.api.service.PaymentService;
import com.traintracker.api.service.PdfTicketService;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/razorpay/webhook")
public class PaymentWebhookController {
	private final PaymentService paymentService;
	private final PaymentRepository paymentRepository;
	private final BookingRepository bookingRepository;
	private final PdfTicketService pdfTicketService;
	private final EmailService emailService;
	private final EventBus eventBus;

	public PaymentWebhookController(PaymentService paymentService, PaymentRepository paymentRepository, BookingRepository bookingRepository, PdfTicketService pdfTicketService, EmailService emailService, EventBus eventBus) {
		this.paymentService = paymentService;
		this.paymentRepository = paymentRepository;
		this.bookingRepository = bookingRepository;
		this.pdfTicketService = pdfTicketService;
		this.emailService = emailService;
		this.eventBus = eventBus;
	}

	@PostMapping
	public ResponseEntity<?> handle(@RequestHeader(name = "X-Razorpay-Signature", required = false) String signature,
				@RequestBody byte[] payloadBytes) {
		String body = new String(payloadBytes, StandardCharsets.UTF_8);
		if (!paymentService.verifyWebhook(body, signature)) {
			return ResponseEntity.status(400).body(Map.of("ok", false));
		}
		JSONObject event = new JSONObject(body);
		String eventType = event.optString("event", "");
		if (!"payment.captured".equals(eventType)) {
			return ResponseEntity.ok(Map.of("ok", true));
		}
		JSONObject paymentObj = event.getJSONObject("payload").getJSONObject("payment").getJSONObject("entity");
		String orderId = paymentObj.getString("order_id");
		String paymentId = paymentObj.getString("id");
		Optional<Payment> latest = paymentRepository.findTopByProviderOrderIdOrderByIdDesc(orderId);
		if (latest.isPresent()) {
			Payment p = latest.get();
			p.setProviderPaymentId(paymentId);
			p.setStatus("SUCCESS");
			paymentRepository.save(p);
			Booking booking = bookingRepository.findById(p.getBooking().getId()).orElse(null);
			if (booking != null) {
				if (booking.getUser() != null && booking.getUser().getEmail() != null) {
					byte[] pdf = pdfTicketService.generateTicketPdf(booking);
					emailService.sendTicket(booking.getUser().getEmail(), "Your Ticket - PNR " + booking.getPnr(), "Attached is your ticket.", pdf, "ticket-" + booking.getPnr() + ".pdf");
				}
				eventBus.publish("payment.success", Map.of("bookingId", booking.getId(), "pnr", booking.getPnr()));
			}
		}
		return ResponseEntity.ok(Map.of("ok", true));
	}
}