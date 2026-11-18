package io.cloudops.ledger;

import java.math.BigDecimal;
import java.util.Set;
import org.springframework.stereotype.Service;

/** Evaluates incoming ledger postings against simple risk and compliance thresholds. */
@Service
public class FraudCheckService {

    private static final BigDecimal SINGLE_TX_LIMIT = new BigDecimal("10000.00");
    private static final Set<String> BLOCKED_MERCHANTS = Set.of("HIGH_RISK_TEST", "SANCTIONED_VENDOR");

    /**
     * Evaluates whether a payment entry clears fraud policy checks.
     *
     * @param merchantCode merchant classification code
     * @param amount positive transaction amount
     * @return true when approved; false when flagged for rejection
     */
    public boolean isApproved(final String merchantCode, final BigDecimal amount) {
        if (merchantCode == null || merchantCode.isBlank()) {
            return false;
        }
        if (BLOCKED_MERCHANTS.contains(merchantCode.trim().toUpperCase())) {
            return false;
        }
        return amount != null
            && amount.compareTo(BigDecimal.ZERO) > 0
            && amount.compareTo(SINGLE_TX_LIMIT) <= 0;
    }
}
