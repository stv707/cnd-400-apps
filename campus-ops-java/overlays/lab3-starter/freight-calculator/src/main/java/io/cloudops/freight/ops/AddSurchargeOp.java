package io.cloudops.freight.ops;

/** Binary addition operator used to add fixed surcharges in Challenge Lab 3. */
public class AddSurchargeOp {

    /** Precedence level lower than multiplication. */
    public static final int PRECEDENCE = 1;

    /**
     * Adds two monetary or numeric operands.
     *
     * @param left left-hand operand
     * @param right right-hand operand
     * @return sum of operands
     */
    public double apply(final double left, final double right) {
        return left + right;
    }
}
