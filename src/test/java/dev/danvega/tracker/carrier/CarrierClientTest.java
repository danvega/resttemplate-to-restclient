package dev.danvega.tracker.carrier;

import java.util.List;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(CarrierClient.class)
@Import({ CarrierClientConfig.class, ApiKeyCustomizer.class })
@EnableConfigurationProperties(CarrierProperties.class)
class CarrierClientTest {

    @Autowired
    CarrierClient carrierClient;

    @Autowired
    MockRestServiceServer server;

    @Test
    void getShipmentReturnsShipment() {
        server.expect(requestTo("https://carrier.example.com/v1/shipments/1Z999AA10123456784"))
                .andExpect(header("X-Api-Key", "test-key"))
                .andRespond(withSuccess("""
                        {
                          "trackingNumber": "1Z999AA10123456784",
                          "status": "IN_TRANSIT",
                          "origin": "Cleveland, OH",
                          "destination": "Austin, TX",
                          "estimatedDelivery": "2026-11-21"
                        }
                        """, MediaType.APPLICATION_JSON));

        Shipment shipment = carrierClient.getShipment("1Z999AA10123456784");

        assertThat(shipment.status()).isEqualTo(ShipmentStatus.IN_TRANSIT);
        assertThat(shipment.destination()).isEqualTo("Austin, TX");
        server.verify();
    }

    @Test
    void getShipmentsReturnsList() {
        server.expect(requestTo("https://carrier.example.com/v1/shipments"))
                .andRespond(withSuccess("""
                        [
                          { "trackingNumber": "1Z999AA10123456784", "status": "IN_TRANSIT" },
                          { "trackingNumber": "1Z999AA10123456785", "status": "DELIVERED" }
                        ]
                        """, MediaType.APPLICATION_JSON));

        List<Shipment> shipments = carrierClient.getShipments();

        assertThat(shipments).extracting(Shipment::status)
                .containsExactly(ShipmentStatus.IN_TRANSIT, ShipmentStatus.DELIVERED);
    }

    @Test
    void unknownTrackingNumberThrowsShipmentNotFound() {
        server.expect(requestTo("https://carrier.example.com/v1/shipments/NOPE"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThatThrownBy(() -> carrierClient.getShipment("NOPE"))
                .isInstanceOf(ShipmentNotFoundException.class)
                .hasMessageContaining("NOPE");
    }

}
