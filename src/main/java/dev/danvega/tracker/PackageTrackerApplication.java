package dev.danvega.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PackageTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PackageTrackerApplication.class, args);
    }

}
