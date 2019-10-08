package com.github.ivanjermakov.microelevator.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WebClientService {

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

}
