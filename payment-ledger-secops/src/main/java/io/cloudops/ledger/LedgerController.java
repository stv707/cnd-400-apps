package io.cloudops.ledger;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST API for listing and recording verified payment ledger postings. */
@RestController
@RequestMapping("/api/ledger")
public class LedgerController {

    private final FraudCheckService fraudCheckService;
    private final List<LedgerEntry> entries = new CopyOnWriteArrayList<>();

    public LedgerController(final FraudCheckService fraudCheckService) {
        this.fraudCheckService = fraudCheckService;
    }

    public record PostLedgerRequest(String accountId, String merchantCode, BigDecimal amount) {
    }

    public record LedgerEntry(
        String id,
        String accountId,
        String merchantCode,
        BigDecimal amount,
        Instant recordedAt
    ) {
    }

    @GetMapping("/entries")
    public Map<String, Object> listEntries() {
        return Map.of("count", entries.size(), "entries", List.copyOf(entries));
    }

    @PostMapping("/entries")
    public ResponseEntity<Object> recordEntry(@RequestBody final PostLedgerRequest request) {
        if (request == null || request.accountId() == null || request.accountId().isBlank()
                || request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Valid accountId and positive amount required"));
        }

        if (!fraudCheckService.isApproved(request.merchantCode(), request.amount())) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Map.of("error", "Transaction rejected by fraud policy"));
        }

        LedgerEntry entry = new LedgerEntry(
            UUID.randomUUID().toString(),
            request.accountId().trim(),
            request.merchantCode().trim().toUpperCase(),
            request.amount(),
            Instant.now());
        entries.add(entry);
        return ResponseEntity.status(HttpStatus.CREATED).body(entry);
    }
}
