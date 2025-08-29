package com.traintracker.api.web;

import com.traintracker.api.domain.Booking;
import com.traintracker.api.domain.Station;
import com.traintracker.api.domain.Train;
import com.traintracker.api.repo.BookingRepository;
import com.traintracker.api.repo.StationRepository;
import com.traintracker.api.repo.TrainRepository;
import com.traintracker.api.service.EventBus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/bookings")
@Validated
public class BookingController {
	private final BookingRepository bookingRepository;
	private final TrainRepository trainRepository;
	private final StationRepository stationRepository;
	private final EventBus eventBus;

	public BookingController(BookingRepository bookingRepository, TrainRepository trainRepository, StationRepository stationRepository, EventBus eventBus) {
		this.bookingRepository = bookingRepository;
		this.trainRepository = trainRepository;
		this.stationRepository = stationRepository;
		this.eventBus = eventBus;
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
		eventBus.publish("booking.created", Map.of("bookingId", booking.getId(), "pnr", booking.getPnr()));
		return ResponseEntity.ok(Map.of(
				"bookingId", booking.getId(),
				"pnr", booking.getPnr(),
				"amount", booking.getTotalAmount()
		));
	}

	@GetMapping("/{id}")
	public ResponseEntity<BookingSummary> getById(@PathVariable Long id) {
		Booking b = bookingRepository.findById(id).orElseThrow();
		return ResponseEntity.ok(toSummary(b));
	}

	@GetMapping
	public ResponseEntity<List<BookingSummary>> listRecent(@RequestParam(name = "limit", defaultValue = "20") int limit) {
		if (limit < 1) limit = 1;
		if (limit > 100) limit = 100;
		List<Booking> list = bookingRepository.findAll(PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"))).getContent();
		return ResponseEntity.ok(list.stream().map(this::toSummary).toList());
	}

	private BookingSummary toSummary(Booking b) {
		return new BookingSummary(
				b.getId(),
				b.getPnr(),
				b.getTrain() != null ? b.getTrain().getNumber() : null,
				b.getFromStation() != null ? b.getFromStation().getCode() : null,
				b.getToStation() != null ? b.getToStation().getCode() : null,
				b.getTravelDate(),
				b.getStatus(),
				b.getTotalAmount(),
				b.getCreatedAt()
		);
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

	public record BookingSummary(
			Long id,
			String pnr,
			String trainNumber,
			String fromCode,
			String toCode,
			LocalDate travelDate,
			String status,
			BigDecimal amount,
			OffsetDateTime createdAt
	) {}
}