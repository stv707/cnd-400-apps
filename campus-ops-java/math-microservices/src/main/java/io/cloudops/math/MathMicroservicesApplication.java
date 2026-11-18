package io.cloudops.math;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/** Consolidated Spring Boot host for the 3-endpoint Math Microservices lab. */
@SpringBootApplication
public class MathMicroservicesApplication {

    /**
     * Entry point for running Adder, Multiplier, and Gateway Solver services.
     *
     * @param args CLI flags
     */
    public static void main(final String[] args) {
        SpringApplication.run(MathMicroservicesApplication.class, args);
    }

    /** Shared RestTemplate bean used by GatewaySolverService. */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
