package dev.danvega.tracker.carrier;

import org.springframework.boot.restclient.RestTemplateCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ApiKeyCustomizer implements RestTemplateCustomizer {

    private final CarrierProperties properties;

    public ApiKeyCustomizer(CarrierProperties properties) {
        this.properties = properties;
    }

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set("X-Api-Key", properties.apiKey());
            return execution.execute(request, body);
        });
    }

}
