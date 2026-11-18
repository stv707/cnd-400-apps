package io.cloudops.math;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** Microservice #1: sums two integers cleanly over JSON HTTP. */
@RestController
@RequestMapping("/api/add")
public class AdderController {

    /** Request payload for integer addition. */
    public record AddRequest(Integer left, Integer right) {
    }

    /** Adds two integer operands. */
    @PostMapping
    public Map<String, Integer> add(@RequestBody final AddRequest request) {
        if (request == null || request.left() == null || request.right() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "left and right are required");
        }
        return Map.of("result", request.left() + request.right());
    }
}
