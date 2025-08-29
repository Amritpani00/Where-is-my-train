package com.traintracker.api.web;

import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {
	private final Environment env;

	public HealthController(Environment env) {
		this.env = env;
	}

	@GetMapping
	public ResponseEntity<Map<String, Object>> health() {
		String[] profiles = env.getActiveProfiles();
		return ResponseEntity.ok(Map.of(
				"status", "ok",
				"profiles", profiles
		));
	}
}