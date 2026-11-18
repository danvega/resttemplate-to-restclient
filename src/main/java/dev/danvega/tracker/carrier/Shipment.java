package dev.danvega.tracker.carrier;

import java.time.LocalDate;

public record Shipment(
        String trackingNumber,
        ShipmentStatus status,
        String origin,
        String destination,
        LocalDate estimatedDelivery) {
}
