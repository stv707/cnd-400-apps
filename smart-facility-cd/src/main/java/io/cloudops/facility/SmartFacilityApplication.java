package io.cloudops.facility;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point for the Smart Facility HVAC & Sensor CD service. */
@SpringBootApplication
public class SmartFacilityApplication {

    public static void main(final String[] args) {
        SpringApplication.run(SmartFacilityApplication.class, args);
    }
}
