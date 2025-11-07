
public abstract class Unop extends Op {
    public boolean isLeaf() { return true; }

    public abstract double eval(double[] x);
}
