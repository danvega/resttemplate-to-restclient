package dev.danvega.tracker.carrier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class CarrierClientConfig {

    @Bean
    RestClient carrierRestClient(RestClient.Builder builder, CarrierProperties properties) {
        return builder
                .baseUrl(properties.baseUrl())
                .build();
    }

}
