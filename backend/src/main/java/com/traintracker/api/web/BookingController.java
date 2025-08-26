package com.traintracker.api.web;

import com.traintracker.api.domain.Booking;
import com.traintracker.api.domain.Station;
import com.traintracker.api.domain.Train;
import com.traintracker.api.repo.BookingRepository;
import com.traintracker.api.repo.StationRepository;
import com.traintracker.api.repo.TrainRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/bookings")
@Validated
public class BookingController {
	private final BookingRepository bookingRepository;
	private final TrainRepository trainRepository;
	private final StationRepository stationRepository;

	public BookingController(BookingRepository bookingRepository, TrainRepository trainRepository, StationRepository stationRepository) {
		this.bookingRepository = bookingRepository;
		this.trainRepository = trainRepository;
		this.stationRepository = stationRepository;
	}

	@PostMapping
	public ResponseEntity<?> create(@Valid @RequestBody CreateBookingRequest req) {
		Train train = trainRepository.findByNumber(req.trainNumber()).orElseGet(() -> trainRepository.save(Train.builder()
				.number(req.trainNumber())
				.name(req.trainName() != null && !req.trainName().isBlank() ? req.trainName() : ("Train " + req.trainNumber()))
				.active(true)
				.build()));
		Station from = stationRepository.findByCode(req.fromCode()).orElseGet(() -> stationRepository.save(Station.builder()
				.code(req.fromCode())
				.name(req.fromCode())
				.build()));
		Station to = stationRepository.findByCode(req.toCode()).orElseGet(() -> stationRepository.save(Station.builder()
				.code(req.toCode())
				.name(req.toCode())
				.build()));

		Booking booking = Booking.builder()
				.pnr(generatePnr())
				.train(train)
				.fromStation(from)
				.toStation(to)
				.travelDate(LocalDate.parse(req.travelDate()))
				.status("CREATED")
				.totalAmount(new BigDecimal(req.amount()))
				.createdAt(OffsetDateTime.now())
				.build();
		bookingRepository.save(booking);
		return ResponseEntity.ok(Map.of(
				"bookingId", booking.getId(),
				"pnr", booking.getPnr(),
				"amount", booking.getTotalAmount()
		));
	}

	private static String generatePnr() {
		Random r = new Random();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 10; i++) sb.append(r.nextInt(10));
		return sb.toString();
	}

	public record CreateBookingRequest(
			@NotBlank String trainNumber,
			String trainName,
			@NotBlank String fromCode,
			@NotBlank String toCode,
			@NotBlank String travelDate,
			@NotNull String amount
	) {}
}