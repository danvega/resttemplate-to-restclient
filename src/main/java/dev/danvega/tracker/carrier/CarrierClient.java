package dev.danvega.tracker.carrier;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CarrierClient {

    private final RestTemplate restTemplate;

    public CarrierClient(RestTemplate carrierRestTemplate) {
        this.restTemplate = carrierRestTemplate;
    }

    public Shipment getShipment(String trackingNumber) {
        return restTemplate.getForObject("/shipments/{trackingNumber}", Shipment.class, trackingNumber);
    }

    public List<Shipment> getShipments() {
        return restTemplate.exchange(
                "/shipments",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Shipment>>() {})
                .getBody();
    }

}
