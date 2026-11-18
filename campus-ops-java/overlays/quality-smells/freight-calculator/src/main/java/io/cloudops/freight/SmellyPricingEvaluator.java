package io.cloudops.freight;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Intentionally flawed class for Ex 4.2 (Checkstyle, PMD, CPD, and JaCoCo gate exercise).
 */
public class SmellyPricingEvaluator {

    public BigDecimal evaluateDomesticCost(double weightKg, boolean express, boolean insured, boolean fragile, boolean weekend) {
        if (weightKg <= 0)
            return BigDecimal.ZERO;

        BigDecimal fee = new BigDecimal("5.00");
        fee = fee.add(BigDecimal.valueOf(weightKg).multiply(new BigDecimal("2.25")));
        if (weightKg > 20) {
            if (express) {
                if (insured) {
                    if (fragile) {
                        if (weekend) {
                            fee = fee.add(new BigDecimal("40.00"));
                        } else {
                            fee = fee.add(new BigDecimal("30.00"));
                        }
                    } else {
                        fee = fee.add(new BigDecimal("20.00"));
                    }
                } else {
                    fee = fee.add(new BigDecimal("12.50"));
                }
            }
        }
        return fee;
    }

    // Intentional copy-paste duplicate block for PMD CPD violation
    public BigDecimal evaluateRegionalCost(double weightKg, boolean express, boolean insured, boolean fragile, boolean weekend) {
        if (weightKg <= 0)
            return BigDecimal.ZERO;

        BigDecimal fee = new BigDecimal("5.00");
        fee = fee.add(BigDecimal.valueOf(weightKg).multiply(new BigDecimal("2.25")));
        if (weightKg > 20) {
            if (express) {
                if (insured) {
                    if (fragile) {
                        if (weekend) {
                            fee = fee.add(new BigDecimal("40.00"));
                        } else {
                            fee = fee.add(new BigDecimal("30.00"));
                        }
                    } else {
                        fee = fee.add(new BigDecimal("20.00"));
                    }
                } else {
                    fee = fee.add(new BigDecimal("12.50"));
                }
            }
        }
        return fee;
    }
}
