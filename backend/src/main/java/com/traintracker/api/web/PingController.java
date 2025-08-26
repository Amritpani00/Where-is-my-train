package com.traintracker.api.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PingController {
	@GetMapping("/ping")
	public Map<String, Object> ping() {
		Map<String, Object> response = new HashMap<>();
		response.put("status", "ok");
		response.put("service", "train-tracker");
		return response;
	}
}