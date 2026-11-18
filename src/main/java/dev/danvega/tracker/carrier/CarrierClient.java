package dev.danvega.tracker.carrier;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CarrierClient {

    private final RestClient restClient;

    public CarrierClient(RestClient carrierRestClient) {
        this.restClient = carrierRestClient;
    }

    public Shipment getShipment(String trackingNumber) {
        return restClient.get()
                .uri("/shipments/{trackingNumber}", trackingNumber)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND), (request, response) -> {
                    throw new ShipmentNotFoundException(trackingNumber);
                })
                .body(Shipment.class);
    }

    public List<Shipment> getShipments() {
        return restClient.get()
                .uri("/shipments")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

}
