package io.cloudops.ordercart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point for the Campus Ops Order Cart microservice. */
@SpringBootApplication
public class OrderCartApplication {

    /**
     * Launches the Order Cart Spring Boot application.
     *
     * @param args CLI arguments
     */
    public static void main(final String[] args) {
        SpringApplication.run(OrderCartApplication.class, args);
    }
}
