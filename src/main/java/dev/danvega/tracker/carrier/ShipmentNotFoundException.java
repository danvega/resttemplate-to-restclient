package dev.danvega.tracker.carrier;

public class ShipmentNotFoundException extends RuntimeException {

    public ShipmentNotFoundException(String trackingNumber) {
        super("No shipment found with tracking number " + trackingNumber);
    }

}
