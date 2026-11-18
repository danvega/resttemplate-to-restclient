package dev.danvega.tracker;

import java.time.LocalDate;

import dev.danvega.tracker.carrier.CarrierClient;
import dev.danvega.tracker.carrier.Shipment;
import dev.danvega.tracker.carrier.ShipmentNotFoundException;
import dev.danvega.tracker.carrier.ShipmentStatus;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class ShipmentControllerTest {

    @Autowired
    RestTestClient client;

    @MockitoBean
    CarrierClient carrierClient;

    @Test
    void findOneReturnsShipment() {
        given(carrierClient.getShipment("1Z999AA10123456784")).willReturn(new Shipment(
                "1Z999AA10123456784", ShipmentStatus.OUT_FOR_DELIVERY,
                "Cleveland, OH", "Austin, TX", LocalDate.of(2026, 11, 21)));

        client.get().uri("/api/shipments/{id}", "1Z999AA10123456784")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Shipment.class)
                .value(shipment -> assertThat(shipment.status()).isEqualTo(ShipmentStatus.OUT_FOR_DELIVERY));
    }

    @Test
    void findOneReturns404ForUnknownShipment() {
        given(carrierClient.getShipment("NOPE")).willThrow(new ShipmentNotFoundException("NOPE"));

        client.get().uri("/api/shipments/{id}", "NOPE")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.detail").isEqualTo("No shipment found with tracking number NOPE");
    }

}
