package io.cloudops.freight;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FreightCalculatorTest {

    private FreightCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new FreightCalculator();
    }

    @ParameterizedTest
    @CsvSource({
        "2.0, DOMESTIC, false, 9.50",
        "4.0, REGIONAL, false, 23.00",
        "2.0, INTERNATIONAL, true, 35.00"
    })
    void calculatesStandardAndExpressRates(
            final double weightKg,
            final Region region,
            final boolean express,
            final String expected) {
        assertEquals(new BigDecimal(expected), calculator.calculateQuote(weightKg, region, express));
    }

    @Test
    void appliesBulkDiscountBeforeExpressSurchargeAtTwentyKgOrMore() {
        // (5.00 + 20 * 2.25) * 0.90 + 12.50 = 50.00 * 0.90 + 12.50 = 57.50
        BigDecimal quote = calculator.calculateQuote(20.0d, Region.DOMESTIC, true);
        assertEquals(new BigDecimal("57.50"), quote);
    }

    @Test
    void rejectsNonPositiveWeightAndNullRegion() {
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateQuote(0.0d, Region.DOMESTIC, false));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateQuote(-3.5d, Region.REGIONAL, true));
        assertThrows(IllegalArgumentException.class, () -> calculator.calculateQuote(Double.NaN, Region.DOMESTIC, false));
        assertThrows(UnsupportedRegionException.class, () -> calculator.calculateQuote(5.0d, null, false));
    }
}
