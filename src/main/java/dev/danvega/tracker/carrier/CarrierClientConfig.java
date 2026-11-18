package dev.danvega.tracker.carrier;

import java.time.Duration;

import org.springframework.boot.restclient.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CarrierClientConfig {

    @Bean
    RestTemplate carrierRestTemplate(RestTemplateBuilder builder, CarrierProperties properties) {
        return builder
                .rootUri(properties.baseUrl())
                .connectTimeout(Duration.ofSeconds(2))
                .readTimeout(Duration.ofSeconds(5))
                .errorHandler(new CarrierErrorHandler())
                .build();
    }

}
