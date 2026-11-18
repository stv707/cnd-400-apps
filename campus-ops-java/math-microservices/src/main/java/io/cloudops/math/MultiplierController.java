package io.cloudops.math;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Microservice #2: multiplies operandA and operandB over JSON HTTP. */
@RestController
@RequestMapping("/api/mul")
public class MultiplierController {

    /** Canonical request contract expected by MultiplierController. */
    public record MultiplyRequest(Integer operandA, Integer operandB) {
    }

    /** Multiplies operandA by operandB; rejects payloads missing canonical keys. */
    @PostMapping
    public Map<String, Integer> multiply(@RequestBody final MultiplyRequest request) {
        if (request == null || request.operandA() == null || request.operandB() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "operandA and operandB are required");
        }
        return Map.of("result", request.operandA() * request.operandB());
    }
}
