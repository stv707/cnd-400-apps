package io.cloudops.freight.ops;

/**
 * Starter left-to-right tariff formula evaluator for Challenge Lab 3 (TDD).
 *
 * <p>Currently ignores operator precedence ({@code +} vs {@code *}); refactor via TDD
 * so multiplication binds tighter than addition.
 */
public class TariffFormulaEvaluator {

    private final AddSurchargeOp addOp = new AddSurchargeOp();
    private final MultiplyRateOp multiplyOp = new MultiplyRateOp();

    /**
     * Evaluates a space-delimited arithmetic formula such as {@code "5 + 3 * 2.5"}.
     *
     * @param formula space-separated tokens
     * @return evaluated total
     */
    public double evaluate(final String formula) {
        if (formula == null || formula.isBlank()) {
            throw new IllegalArgumentException("Formula must not be blank");
        }
        String[] tokens = formula.trim().split("\\s+");
        if (tokens.length % 2 == 0) {
            throw new IllegalArgumentException("Malformed formula: " + formula);
        }

        double acc = Double.parseDouble(tokens[0]);
        for (int i = 1; i < tokens.length; i += 2) {
            String op = tokens[i];
            double rhs = Double.parseDouble(tokens[i + 1]);
            acc = switch (op) {
                case "+" -> addOp.apply(acc, rhs);
                case "*" -> multiplyOp.apply(acc, rhs);
                default -> throw new IllegalArgumentException("Unsupported operator: " + op);
            };
        }
        return acc;
    }
}
