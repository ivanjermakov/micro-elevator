package com.github.ivanjermakov.microelevator.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class WebClientService {

	private static final Logger LOG = LoggerFactory.getLogger(WebClientService.class);

	@Value("${reconnection.timeout}")
	public Integer reconnectionTimeout;

	@Value("${server.url}")
	private String serverUrl;

	public WebClient logicServiceClient() {
		return WebClient.builder()
				.baseUrl(serverUrl + ":" + 8081 + "/logic")
				.build();
	}

	public WebClient elevatorServiceClient() {
		return WebClient.builder()
				.baseUrl(serverUrl + ":" + 8082 + "/elevator")
				.build();
	}

	public WebClient floorServiceClient() {
		return WebClient.builder()
				.baseUrl(serverUrl + ":" + 8083 + "/floor")
				.build();
	}

	public <T> Flux<T> build(WebClient webClient, String uri, ParameterizedTypeReference<ServerSentEvent<T>> typeReference) {
		LOG.info("subscribing to {}", uri);
		return webClient
				.get()
				.uri(uri)
				.retrieve()
				.bodyToFlux(typeReference)
				.map(ServerSentEvent::data)
				.retry()
				.repeat();
	}

}
