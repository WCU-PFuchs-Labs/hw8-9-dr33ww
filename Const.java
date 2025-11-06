package binary;
/**
 * Code Template Author: David G. Cooper
 * Purpose: An operation class representing a constant number
 */
public class Const extends Unop {
    /** the constant value */
    private double value;
    /**
     * @param d the value to set the constant to.
     */
    public Const(double d) {
        value = d;
    }
    /**
     * @return the value of the constant.
     */
    public double eval() {
        return value;
    }
}
