package io.cloudops.facility;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

/** REST API for reading and adjusting Smart Facility zone thermostats. */
@RestController
@RequestMapping("/api/sensors")
public class SensorController {

    private final Map<String, Double> zoneTargets = new ConcurrentHashMap<>(Map.of(
        "zone-a", 22.0d,
        "zone-b", 21.5d
    ));

    private final String appVersion;

    public SensorController(@Value("${APP_VERSION:1.0.0}") final String appVersion) {
        this.appVersion = appVersion;
    }

    public record TargetRequest(Double targetCelsius) {
    }

    @GetMapping
    public Map<String, Object> listZones() {
        return Map.of("version", appVersion, "zones", zoneTargets);
    }

    @GetMapping("/{zoneId}")
    public Map<String, Object> getZone(@PathVariable final String zoneId) {
        Double target = zoneTargets.get(zoneId);
        if (target == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unknown zone: " + zoneId);
        }
        return Map.of("zone", zoneId, "targetCelsius", target, "version", appVersion);
    }

    @PostMapping("/{zoneId}/target")
    public ResponseEntity<Map<String, Object>> setTarget(
            @PathVariable final String zoneId,
            @RequestBody final TargetRequest request) {
        if (request == null || request.targetCelsius() == null
                || request.targetCelsius() < 15.0d || request.targetCelsius() > 30.0d) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "targetCelsius must be between 15.0 and 30.0");
        }
        zoneTargets.put(zoneId, request.targetCelsius());
        return ResponseEntity.ok(Map.of(
            "zone", zoneId,
            "targetCelsius", request.targetCelsius(),
            "version", appVersion));
    }
}
