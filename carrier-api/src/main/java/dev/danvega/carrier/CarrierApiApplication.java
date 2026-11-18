package dev.danvega.carrier;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/**
 * A stand-in for a real carrier's tracking API. The package tracker calls this.
 * It is not part of the migration. Start it and leave it running.
 */
@SpringBootApplication
public class CarrierApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarrierApiApplication.class, args);
    }

    record Shipment(String trackingNumber, String status, String origin, String destination,
            LocalDate estimatedDelivery) {
    }

    @RestController
    @RequestMapping("/v1/shipments")
    static class ShipmentApi {

        private static final String API_KEY = "demo-key";

        private final Map<String, Shipment> shipments = List.of(
                new Shipment("1Z999AA10123456784", "IN_TRANSIT", "Cleveland, OH", "Austin, TX", LocalDate.now().plusDays(2)),
                new Shipment("1Z999AA10123456785", "OUT_FOR_DELIVERY", "Denver, CO", "Chicago, IL", LocalDate.now()),
                new Shipment("1Z999AA10123456786", "DELIVERED", "Seattle, WA", "Portland, OR", LocalDate.now().minusDays(1)),
                new Shipment("1Z999AA10123456787", "LABEL_CREATED", "Miami, FL", "Atlanta, GA", LocalDate.now().plusDays(5)))
                .stream()
                .collect(Collectors.toMap(Shipment::trackingNumber, Function.identity(), (a, b) -> a,
                        java.util.LinkedHashMap::new));

        @GetMapping
        List<Shipment> findAll(@RequestHeader(name = "X-Api-Key", required = false) String apiKey) {
            checkKey(apiKey);
            return List.copyOf(shipments.values());
        }

        @GetMapping("/{trackingNumber}")
        ResponseEntity<Shipment> findOne(@PathVariable String trackingNumber,
                @RequestHeader(name = "X-Api-Key", required = false) String apiKey) {
            checkKey(apiKey);
            Shipment shipment = shipments.get(trackingNumber);
            return (shipment != null) ? ResponseEntity.ok(shipment) : ResponseEntity.notFound().build();
        }

        private void checkKey(String apiKey) {
            if (!API_KEY.equals(apiKey)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing or invalid X-Api-Key");
            }
        }

    }

}
