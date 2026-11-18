package io.cloudops.freight;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.Map;

/** Calculates parcel freight charges using weight brackets, region multipliers, and express surcharges. */
public class FreightCalculator {

    private static final BigDecimal BASE_HANDLING_FEE = new BigDecimal("5.00");
    private static final BigDecimal EXPRESS_SURCHARGE = new BigDecimal("12.50");
    private static final double BULK_DISCOUNT_THRESHOLD_KG = 20.0d;
    private static final BigDecimal BULK_DISCOUNT_FACTOR = new BigDecimal("0.90");

    private static final Map<Region, BigDecimal> RATE_PER_KG = new EnumMap<>(Region.class);

    static {
        RATE_PER_KG.put(Region.DOMESTIC, new BigDecimal("2.25"));
        RATE_PER_KG.put(Region.REGIONAL, new BigDecimal("4.50"));
        RATE_PER_KG.put(Region.INTERNATIONAL, new BigDecimal("8.75"));
    }

    /**
     * Computes a freight quote rounded to two decimal places.
     *
     * @param weightKg shipment weight in kilograms (must be strictly positive)
     * @param region destination shipping zone
     * @param express true when priority air dispatch is requested
     * @return calculated shipping total
     */
    public BigDecimal calculateQuote(final double weightKg, final Region region, final boolean express) {
        if (weightKg <= 0.0d || Double.isNaN(weightKg) || Double.isInfinite(weightKg)) {
            throw new IllegalArgumentException("Weight must be a finite positive value: " + weightKg);
        }
        if (region == null) {
            throw new UnsupportedRegionException("Destination region must not be null");
        }

        BigDecimal perKgRate = RATE_PER_KG.get(region);
        BigDecimal variableCharge = perKgRate.multiply(BigDecimal.valueOf(weightKg));
        BigDecimal subtotal = BASE_HANDLING_FEE.add(variableCharge);

        if (weightKg >= BULK_DISCOUNT_THRESHOLD_KG) {
            subtotal = subtotal.multiply(BULK_DISCOUNT_FACTOR);
        }
        if (express) {
            subtotal = subtotal.add(EXPRESS_SURCHARGE);
        }
        return subtotal.setScale(2, RoundingMode.HALF_UP);
    }
}
