package io.cloudops.ledger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Entry point for the Payment Ledger DevSecOps microservice. */
@SpringBootApplication
public class PaymentLedgerApplication {

    public static void main(final String[] args) {
        SpringApplication.run(PaymentLedgerApplication.class, args);
    }
}
