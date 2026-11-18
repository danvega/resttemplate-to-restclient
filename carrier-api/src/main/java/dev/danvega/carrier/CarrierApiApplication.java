package dev.danvega.carrier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A stand-in for a real carrier's tracking API. The package tracker calls this.
 * It is not part of the migration. Start it and leave it running.
 */
@SpringBootApplication
public class CarrierApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarrierApiApplication.class, args);
    }

}
