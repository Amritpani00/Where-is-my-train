package com.traintracker.api.service;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.traintracker.api.domain.Booking;
import com.traintracker.api.domain.BookedSeat;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfTicketService {
	public byte[] generateTicketPdf(Booking booking) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Document document = new Document();
			PdfWriter.getInstance(document, baos);
			document.open();
			document.add(new Paragraph("Train Ticket"));
			document.add(new Paragraph("PNR: " + booking.getPnr()));
			document.add(new Paragraph("Train: " + booking.getTrain().getNumber() + " - " + booking.getTrain().getName()));
			document.add(new Paragraph("From: " + booking.getFromStation().getCode() + " To: " + booking.getToStation().getCode()));
			document.add(new Paragraph("Date: " + booking.getTravelDate()));
			document.add(new Paragraph("Seats:"));
			for (BookedSeat bs : booking.getBookedSeats()) {
				document.add(new Paragraph(" - " + bs.getSeat().getCoach().getCoachType() + "-" + bs.getSeat().getSeatNumber() + " (" + bs.getPassengerName() + ")"));
			}
			document.close();
			return baos.toByteArray();
		} catch (Exception e) {
			throw new RuntimeException("Failed to generate PDF", e);
		}
	}
}