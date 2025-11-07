public class Variable extends Unop {
    private int index;

    public Variable(int i) { index = i; }

    public double eval(double[] x) {
        return x[index];
    }

    public String toString() {
        return "X" + index;
    }
}
