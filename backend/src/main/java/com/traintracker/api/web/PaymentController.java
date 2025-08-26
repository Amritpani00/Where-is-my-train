package com.traintracker.api.web;

import com.razorpay.Order;
import com.traintracker.api.domain.Booking;
import com.traintracker.api.repo.BookingRepository;
import com.traintracker.api.service.EmailService;
import com.traintracker.api.service.EventBus;
import com.traintracker.api.service.PaymentService;
import com.traintracker.api.service.PdfTicketService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@Validated
public class PaymentController {
	private final PaymentService paymentService;
	private final BookingRepository bookingRepository;
	private final PdfTicketService pdfTicketService;
	private final EmailService emailService;
	private final EventBus eventBus;

	public PaymentController(PaymentService paymentService, BookingRepository bookingRepository, PdfTicketService pdfTicketService, EmailService emailService, EventBus eventBus) {
		this.paymentService = paymentService;
		this.bookingRepository = bookingRepository;
		this.pdfTicketService = pdfTicketService;
		this.emailService = emailService;
		this.eventBus = eventBus;
	}

	@PostMapping("/create-order/{bookingId}")
	public Map<String, Object> createOrder(@PathVariable Long bookingId) throws Exception {
		Order order = paymentService.createOrderForBooking(bookingId);
		return Map.of(
				"orderId", order.get("id"),
				"amount", order.get("amount"),
				"currency", order.get("currency"),
				"status", order.get("status")
		);
	}

	@PostMapping("/verify")
	public ResponseEntity<?> verify(
			@RequestParam @NotBlank String razorpay_order_id,
			@RequestParam @NotBlank String razorpay_payment_id,
			@RequestParam @NotBlank String razorpay_signature,
			@RequestParam Long bookingId,
			@RequestParam(required = false) String email
	) {
		boolean ok = paymentService.verifyAndCapture(razorpay_order_id, razorpay_payment_id, razorpay_signature);
		if (!ok) return ResponseEntity.badRequest().body(Map.of("ok", false));

		Booking booking = bookingRepository.findById(bookingId).orElseThrow();
		String recipient = (email != null && !email.isBlank())
				? email
				: (booking.getUser() != null ? booking.getUser().getEmail() : null);
		if (recipient != null && !recipient.isBlank()) {
			byte[] pdf = pdfTicketService.generateTicketPdf(booking);
			emailService.sendTicket(recipient, "Your Ticket - PNR " + booking.getPnr(), "Attached is your ticket.", pdf, "ticket-" + booking.getPnr() + ".pdf");
		}
		eventBus.publish("payment.success", Map.of("bookingId", booking.getId(), "pnr", booking.getPnr()));
		return ResponseEntity.ok(Map.of("ok", true));
	}

	@GetMapping(value = "/ticket/{bookingId}", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> downloadTicket(@PathVariable Long bookingId) {
		Booking booking = bookingRepository.findById(bookingId).orElseThrow();
		byte[] pdf = pdfTicketService.generateTicketPdf(booking);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket-" + booking.getPnr() + ".pdf")
				.body(pdf);
	}
}