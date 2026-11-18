package io.cloudops.freight.ops;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Starter failing RED test for Challenge Lab 3 TDD precedence refactoring. */
class TariffFormulaEvaluatorTest {

    private final TariffFormulaEvaluator evaluator = new TariffFormulaEvaluator();

    @Test
    void multiplicationTakesPrecedenceOverAddition() {
        // Expected: 5 + (3 * 4) = 17.0; starter left-to-right evaluator yields 32.0 (RED).
        assertEquals(17.0d, evaluator.evaluate("5 + 3 * 4"), 0.0001d);
    }
}
