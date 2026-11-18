package io.cloudops.math;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Orchestrates linear evaluation (a * x + b) across Multiplier and Adder services.
 *
 * <p>Starter bug for Challenge Lab 2: calls Multiplier with legacy keys {@code factorA}/{@code factorB}
 * instead of the canonical {@code operandA}/{@code operandB} contract.
 */
@Service
public class GatewaySolverService {

    private final RestTemplate restTemplate;
    private final String adderBaseUrl;
    private final String multiplierBaseUrl;

    /** Spring-injected constructor configurable via math.adder.url and math.multiplier.url. */
    @Autowired
    public GatewaySolverService(
            final RestTemplate restTemplate,
            @Value("${math.adder.url:http://localhost:8080}") final String adderBaseUrl,
            @Value("${math.multiplier.url:http://localhost:8080}") final String multiplierBaseUrl) {
        this.restTemplate = restTemplate;
        this.adderBaseUrl = adderBaseUrl;
        this.multiplierBaseUrl = multiplierBaseUrl;
    }

    /**
     * Evaluates a * x + b by chaining /api/mul followed by /api/add.
     *
     * @param a linear slope
     * @param x input variable
     * @param b y-intercept offset
     * @return computed result
     */
    public int solveLinear(final int a, final int x, final int b) {
        Map<String, Integer> mulPayload = Map.of("factorA", a, "factorB", x);
        Map<?, ?> mulResponse = restTemplate.postForObject(
            multiplierBaseUrl + "/api/mul", mulPayload, Map.class);
        int product = extractResult(mulResponse, "multiplier");

        Map<String, Integer> addPayload = Map.of("left", product, "right", b);
        Map<?, ?> addResponse = restTemplate.postForObject(
            adderBaseUrl + "/api/add", addPayload, Map.class);
        return extractResult(addResponse, "adder");
    }

    private int extractResult(final Map<?, ?> response, final String serviceName) {
        if (response == null || !(response.get("result") instanceof Number number)) {
            throw new IllegalStateException("Missing numeric result from " + serviceName);
        }
        return number.intValue();
    }
}
