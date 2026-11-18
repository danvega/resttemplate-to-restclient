package dev.danvega.carrier;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/v1/shipments")
class ShipmentController {

    private static final String API_KEY = "demo-key";

    private final List<Shipment> shipments = List.of(
            new Shipment("1Z999AA10123456784", "IN_TRANSIT", "Cleveland, OH", "Austin, TX", LocalDate.now().plusDays(2)),
            new Shipment("1Z999AA10123456785", "OUT_FOR_DELIVERY", "Denver, CO", "Chicago, IL", LocalDate.now()),
            new Shipment("1Z999AA10123456786", "DELIVERED", "Seattle, WA", "Portland, OR", LocalDate.now().minusDays(1)),
            new Shipment("1Z999AA10123456787", "LABEL_CREATED", "Miami, FL", "Atlanta, GA", LocalDate.now().plusDays(5)));

    @GetMapping
    List<Shipment> findAll(@RequestHeader(name = "X-Api-Key", required = false) String apiKey) {
        checkApiKey(apiKey);
        return shipments;
    }

    @GetMapping("/{trackingNumber}")
    ResponseEntity<Shipment> findOne(@PathVariable String trackingNumber,
            @RequestHeader(name = "X-Api-Key", required = false) String apiKey) {
        checkApiKey(apiKey);
        return ResponseEntity.of(shipments.stream()
                .filter(shipment -> shipment.trackingNumber().equals(trackingNumber))
                .findFirst());
    }

    private void checkApiKey(String apiKey) {
        if (!API_KEY.equals(apiKey)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid X-Api-Key");
        }
    }

}
