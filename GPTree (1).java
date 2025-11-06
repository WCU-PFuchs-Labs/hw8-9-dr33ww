
import java.util.*;
import tabular.*; // assumes DataSet and DataRow live in package tabular

public class GPTree implements Comparable<GPTree>, Cloneable {
    public Node root;          // keep public/simple for beginner-style wiring
    private double fitness;    // sum of squared errors over a DataSet

    public GPTree() {}

    public GPTree(Node r) {
        this.root = r;
    }

    public double getFitness() {
        return fitness;
    }

    // compute fitness = sum_i (pred(x_i) - y_i)^2
    public void evalFitness(DataSet data) {
        double sum = 0.0;
        for (int i = 0; i < data.size(); i++) {
            DataRow row = data.get(i);
            double[] x = row.getX();
            double y = row.getY();
            double pred = 0.0;
            try {
                // rely on your existing Node.eval(double[]) implementation
                pred = root.eval(x);
            } catch (Exception e) {
                // penalize invalid trees (e.g., divide by zero)
                pred = Double.NaN;
            }
            if (Double.isNaN(pred) || Double.isInfinite(pred)) {
                // big penalty
                sum += 1e12;
            } else {
                double d = pred - y;
                sum += d * d;
            }
        }
        fitness = sum;
    }

    public int compareTo(GPTree other) {
        if (this.fitness < other.fitness) return -1;
        if (this.fitness > other.fitness) return 1;
        return 0;
    }

    public boolean equals(Object o) {
        if (o == null) return false;
        if (!(o instanceof GPTree)) return false;
        GPTree t = (GPTree) o;
        return this.fitness == t.fitness;
    }

    public Object clone() {
        // deep clone by recursively copying Nodes
        GPTree copy = new GPTree();
        copy.root = cloneNode(this.root);
        copy.fitness = this.fitness;
        return copy;
    }

    private Node cloneNode(Node n) {
        if (n == null) return null;
        // we assume Node exposes: getOperation(), getLeft(), getRight(), setLeft(), setRight()
        // and Op hierarchy supports copy via toString constructor path is unknown;
        // so the safest way is to rebuild the node using the existing Op instance.
        // We'll try to use n.getOperation().copy() if present; if not, fall back to reflection-free simple cases.
        Op op = n.getOperation();
        Node newNode;

        // try to duplicate the Op using simple rules:
        if (op instanceof Variable) {
            Variable v = (Variable) op;
            newNode = new Node(new Variable(v.getIndex()));
        } else if (op instanceof Const) {
            Const c = (Const) op;
            newNode = new Node(new Const(c.getValue()));
        } else {
            // assume Binop subclass; keep same operator instance (safe if Op is stateless),
            // otherwise you may add copy() to your Op/Binop classes.
            newNode = new Node(op);
        }

        newNode.setLeft(cloneNode(n.getLeft()));
        newNode.setRight(cloneNode(n.getRight()));
        return newNode;
    }

    public String toString() {
        if (root == null) return "";
        return root.toString();
    }

    // -------- crossover utilities (do not rely on Node internals beyond getters/setters) --------

    private static class NodeRef {
        public Node parent;
        public boolean isLeft; // true if selected node is the left child of parent; false if right; parent==null => it's root
        public Node node;
        public NodeRef(Node p, boolean il, Node n) { parent = p; isLeft = il; node = n; }
    }

    private int countNodes(Node n) {
        if (n == null) return 0;
        return 1 + countNodes(n.getLeft()) + countNodes(n.getRight());
    }

    private NodeRef getRefAt(Node root, int targetIndex) {
        // pre-order traversal
        Deque<NodeRef> stack = new ArrayDeque<NodeRef>();
        stack.push(new NodeRef(null, false, root));
        int idx = 0;
        while (!stack.isEmpty()) {
            NodeRef cur = stack.pop();
            if (idx == targetIndex) return cur;
            idx++;
            // push right then left to simulate pre-order
            if (cur.node.getRight() != null) stack.push(new NodeRef(cur.node, false, cur.node.getRight()));
            if (cur.node.getLeft() != null) stack.push(new NodeRef(cur.node, true, cur.node.getLeft()));
        }
        return null;
    }

    public static void crossover(GPTree a, GPTree b, Random rand) {
        if (a == null || b == null || a.root == null || b.root == null) return;

        int countA = a.countNodes(a.root);
        int countB = b.countNodes(b.root);
        if (countA == 0 || countB == 0) return;

        int idxA = rand.nextInt(countA);
        int idxB = rand.nextInt(countB);

        NodeRef refA = a.getRefAt(a.root, idxA);
        NodeRef refB = b.getRefAt(b.root, idxB);
        if (refA == null || refB == null) return;

        Node subA = refA.node;
        Node subB = refB.node;

        // swap the subtrees
        if (refA.parent == null) {
            a.root = subB;
        } else {
            if (refA.isLeft) refA.parent.setLeft(subB); else refA.parent.setRight(subB);
        }
        if (refB.parent == null) {
            b.root = subA;
        } else {
            if (refB.isLeft) refB.parent.setLeft(subA); else refB.parent.setRight(subA);
        }
    }
}
