package io.cloudops.math;

import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Microservice #3: exposes /api/solve to orchestrate linear equation calls. */
@RestController
@RequestMapping("/api/solve")
public class SolverController {

    private final GatewaySolverService solverService;

    /** Request payload representing a * x + b. */
    public record SolveRequest(int a, int x, int b) {
    }

    /** Constructs SolverController with the orchestrating GatewaySolverService. */
    public SolverController(final GatewaySolverService solverService) {
        this.solverService = solverService;
    }

    /** Evaluates a * x + b via downstream Multiplier and Adder calls. */
    @PostMapping
    public Map<String, Integer> solve(@RequestBody final SolveRequest request) {
        return Map.of("result", solverService.solveLinear(request.a(), request.x(), request.b()));
    }
}
