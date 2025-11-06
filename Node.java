import java.util.Random;

public class Node {
    private Op operation;
    private Node lChild;
    private Node rChild;

    public Node(Op operation) {
        this.operation = operation;
    }

    public Op getOperation() { return operation; }
    public Node getLeft() { return lChild; }
    public Node getRight() { return rChild; }
    public void setLeft(Node n) { lChild = n; }
    public void setRight(Node n) { rChild = n; }

    public double eval(double[] data) {
        if (operation instanceof Binop) {
            double a = (lChild != null) ? lChild.eval(data) : 0.0;
            double b = (rChild != null) ? rChild.eval(data) : 0.0;
            return ((Binop) operation).eval(a, b);
        }
        if (operation instanceof Const || operation instanceof Variable) {
            return ((Unop) operation).eval(data);
        }
        double v = (lChild != null) ? lChild.eval(data) : 0.0;
        return ((Unop) operation).eval(new double[]{ v });
    }

    public void addRandomKids(NodeFactory factory, int depth, Random rand) {
        if (depth < 0) depth = 0;

        if (operation instanceof Binop) {
            if (lChild == null) {
                lChild = (depth <= 1) ? makeTerminal(factory, rand) : factory.getOperator(rand);
            }
            if (rChild == null) {
                rChild = (depth <= 1) ? makeTerminal(factory, rand) : factory.getOperator(rand);
            }
            if (depth > 1) {
                lChild.addRandomKids(factory, depth - 1, rand);
                rChild.addRandomKids(factory, depth - 1, rand);
            }
            return;
        }

        if (operation instanceof Const || operation instanceof Variable) {
            return;
        }

        if (lChild == null) {
            lChild = (depth <= 1) ? makeTerminal(factory, rand) : factory.getOperator(rand);
        }
        if (depth > 1) {
            lChild.addRandomKids(factory, depth - 1, rand);
        }
    }

    private Node makeTerminal(NodeFactory factory, Random rand) {
        for (int i = 0; i < 32; i++) {
            Node n = factory.getOperator(rand);
            if (n.operation instanceof Const || n.operation instanceof Variable) return n;
        }
        return new Node(new Const(0.0));
    }

    private String toPlaceholderString() {
        if (operation instanceof Binop) {
            String leftS = (lChild == null) ? "?" : lChild.toPlaceholderString();
            String rightS = (rChild == null) ? "?" : rChild.toPlaceholderString();
            String sym = binopSymbol((Binop) operation);
            return "(" + leftS + " " + sym + " " + rightS + ")";
        }
        if (operation instanceof Const || operation instanceof Variable) {
            return "?";
        }
        String inner = (lChild == null) ? "?" : lChild.toPlaceholderString();
        return operation.toString() + "(" + inner + ")";
    }

    @Override
    public String toString() {
        return toPlaceholderString();
    }

    private String binopSymbol(Binop bop) {
        if (bop instanceof Plus)   return "+";
        if (bop instanceof Minus)  return "-";
        if (bop instanceof Mult)   return "*";
        if (bop instanceof Divide) return "/";
        return "?";
    }

   
    public void traverse(Collector c) {

        c.collect(this);
        

        if (lChild != null) {
            lChild.traverse(c);
        }
        

        if (rChild != null) {
            rChild.traverse(c);
        }
    }
    
 
    public void swapLeft(Node trunk) {
        Node temp = this.lChild;
        this.lChild = trunk.lChild;
        trunk.lChild = temp;
    }
    

    public void swapRight(Node trunk) {
        Node temp = this.rChild;
        this.rChild = trunk.rChild;
        trunk.rChild = temp;
    }
    

    public boolean isLeaf() {
        return operation instanceof Const || operation instanceof Variable;
    }
    
    public String asPlaceholder() {
        return toPlaceholderString();
    }
}
