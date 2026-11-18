package dev.danvega.carrier;

import java.time.LocalDate;

record Shipment(String trackingNumber, String status, String origin, String destination, LocalDate estimatedDelivery) {
}
