package com.traintracker.api.service;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class EventBus {
	private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

	public SseEmitter subscribe() {
		SseEmitter emitter = new SseEmitter(0L);
		emitter.onCompletion(() -> emitters.remove(emitter));
		emitter.onTimeout(() -> emitters.remove(emitter));
		emitter.onError(e -> emitters.remove(emitter));
		emitters.add(emitter);
		try {
			emitter.send(SseEmitter.event().name("connected").data(Instant.now().toString()));
		} catch (IOException ignored) {}
		return emitter;
	}

	public void publish(String type, Object payload) {
		for (SseEmitter emitter : emitters) {
			try {
				emitter.send(SseEmitter.event().name(type).data(payload));
			} catch (IOException e) {
				emitter.complete();
				emitters.remove(emitter);
			}
		}
	}
}