package dev.danvega.tracker.carrier;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "carrier")
public record CarrierProperties(String baseUrl, String apiKey) {
}
