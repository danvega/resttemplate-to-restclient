package dev.danvega.tracker.carrier;

import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ApiKeyCustomizer implements RestClientCustomizer {

    private final CarrierProperties properties;

    public ApiKeyCustomizer(CarrierProperties properties) {
        this.properties = properties;
    }

    @Override
    public void customize(RestClient.Builder restClientBuilder) {
        restClientBuilder.defaultHeader("X-Api-Key", properties.apiKey());
    }

}
