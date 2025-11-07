
public abstract class Unop extends Op {
    @Override
    public boolean isLeaf() { return true; }

    
    public abstract double eval(double[] x);
}
