package com.traintracker.api.web;

import com.traintracker.api.domain.Booking;
import com.traintracker.api.repo.BookingRepository;
import com.traintracker.api.service.EmailService;
import com.traintracker.api.service.PdfTicketService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@Validated
public class EmailController {
	private final BookingRepository bookingRepository;
	private final PdfTicketService pdfTicketService;
	private final EmailService emailService;

	public EmailController(BookingRepository bookingRepository, PdfTicketService pdfTicketService, EmailService emailService) {
		this.bookingRepository = bookingRepository;
		this.pdfTicketService = pdfTicketService;
		this.emailService = emailService;
	}

	@PostMapping("/ticket/{bookingId}")
	public ResponseEntity<?> sendTicket(
			@PathVariable Long bookingId,
			@RequestParam @NotBlank @Email String to
	) {
		Booking booking = bookingRepository.findById(bookingId).orElseThrow();
		byte[] pdf = pdfTicketService.generateTicketPdf(booking);
		emailService.sendTicket(
				to,
				"Your Ticket - PNR " + booking.getPnr(),
				"Attached is your ticket.",
				pdf,
				"ticket-" + booking.getPnr() + ".pdf"
		);
		return ResponseEntity.ok(Map.of("ok", true));
	}
}