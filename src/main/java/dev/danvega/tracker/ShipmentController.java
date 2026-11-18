package dev.danvega.tracker;

import java.util.List;

import dev.danvega.tracker.carrier.CarrierClient;
import dev.danvega.tracker.carrier.Shipment;
import dev.danvega.tracker.carrier.ShipmentNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final CarrierClient carrierClient;

    public ShipmentController(CarrierClient carrierClient) {
        this.carrierClient = carrierClient;
    }

    @GetMapping
    public List<Shipment> findAll() {
        return carrierClient.getShipments();
    }

    @GetMapping("/{trackingNumber}")
    public Shipment findOne(@PathVariable String trackingNumber) {
        return carrierClient.getShipment(trackingNumber);
    }

    @ExceptionHandler(ShipmentNotFoundException.class)
    public ProblemDetail handleNotFound(ShipmentNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

}
