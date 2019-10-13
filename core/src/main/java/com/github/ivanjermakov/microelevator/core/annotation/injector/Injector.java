package com.github.ivanjermakov.microelevator.core.annotation.injector;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ivanjermakov.microelevator.core.annotation.annotation.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.lang.reflect.Field;
import java.time.Duration;

@Component
public class Injector {

	private static final Logger LOG = LoggerFactory.getLogger(Injector.class);

	public static void inject(Object instance) {
		Field[] fields = instance.getClass().getDeclaredFields();

		for (Field field : fields) {
			if (field.isAnnotationPresent(Subscribe.class)) {
				LOG.info("found annotation on: {}", instance.getClass().getSimpleName());

				Subscribe subscribeAnnotation = field.getAnnotation(Subscribe.class);
				field.setAccessible(true);
				try {
					field.set(instance, build(subscribeAnnotation));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static Flux build(Subscribe subscribe) {
		LOG.info("building subscription flux of type {}", subscribe.responseType());

		return WebClient
				.create(subscribe.baseUrl())
				.get()
				.uri(subscribe.uri())
				.retrieve()
				.bodyToFlux(new ParameterizedTypeReference<ServerSentEvent<Object>>() {})
				.map(ServerSentEvent::data)
				.map(o -> new ObjectMapper()
						.enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
						.convertValue(o, subscribe.responseType()))
				.doOnError(e -> LOG.error("error", e))
				.retryBackoff(Long.MAX_VALUE, Duration.ofSeconds(1))
				.repeat()
				.log();
	}

}