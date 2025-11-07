
public class Const extends Unop {
    private double value;

    public Const(double v) { value = v; }

    public double eval(double[] x) {
        return value;
    }

    public String toString() {
        return String.format("%.2f", value);
    }
}
