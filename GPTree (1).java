
import java.util.*;

public class GPTree implements Comparable<GPTree>, Cloneable {
    public Node root;
    private double fitness;    // sum of squared errors over a DataSet

    public GPTree() {}
    public GPTree(Node r) { this.root = r; }

    public double getFitness() { return fitness; }

    // compute fitness = sum_i (pred(x_i) - y_i)^2
    public void evalFitness(DataSet data) {
        double sum = 0.0;
        for (int i = 0; i < data.size(); i++) {
            DataRow row = data.get(i);
            double[] x = row.getX();
            double y = row.getY();
            double pred;
            try {
                pred = root.eval(x);
            } catch (Exception e) {
                pred = Double.NaN;
            }
            if (Double.isNaN(pred) || Double.isInfinite(pred)) {
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
        GPTree copy = new GPTree();
        copy.root = cloneNode(this.root);
        copy.fitness = this.fitness;
        return copy;
    }

    private Node cloneNode(Node n) {
        if (n == null) return null;
        // reuse same operation object (assumed stateless), deep-clone children
        Node newNode = new Node(n.getOperation());
        newNode.setLeft(cloneNode(n.getLeft()));
        newNode.setRight(cloneNode(n.getRight()));
        return newNode;
    }

    public String toString() { return (root == null) ? "" : root.toString(); }

    // -------- crossover utilities (Node getters/setters only) --------

    private static class NodeRef {
        public Node parent;
        public boolean isLeft;
        public Node node;
        public NodeRef(Node p, boolean il, Node n) { parent = p; isLeft = il; node = n; }
    }

    private int countNodes(Node n) {
        if (n == null) return 0;
        return 1 + countNodes(n.getLeft()) + countNodes(n.getRight());
    }

    private NodeRef getRefAt(Node root, int targetIndex) {
        Deque<NodeRef> stack = new ArrayDeque<NodeRef>();
        stack.push(new NodeRef(null, false, root));
        int idx = 0;
        while (!stack.isEmpty()) {
            NodeRef cur = stack.pop();
            if (idx == targetIndex) return cur;
            idx++;
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
        if (refA.parent == null) a.root = subB;
        else { if (refA.isLeft) refA.parent.setLeft(subB); else refA.parent.setRight(subB); }
        if (refB.parent == null) b.root = subA;
        else { if (refB.isLeft) refB.parent.setLeft(subA); else refB.parent.setRight(subA); }
    }
}
