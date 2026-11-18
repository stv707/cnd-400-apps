package io.cloudops.freight.ops;

/** Binary multiplication operator used for zone/weight rates in Challenge Lab 3. */
public class MultiplyRateOp {

    /** Higher precedence level than additive surcharge operators. */
    public static final int PRECEDENCE = 2;

    /**
     * Multiplies two numeric operands.
     *
     * @param left left-hand operand
     * @param right right-hand operand
     * @return product of operands
     */
    public double apply(final double left, final double right) {
        return left * right;
    }
}
