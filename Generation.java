
import java.util.*;


public class Generation {
    public GPTree[] trees;
    private DataSet data;
    private Random rand;
    private NodeFactory factory;
    private int popSize;
    private int maxDepth;
    private int numVars;

    public Generation(int popSize, int maxDepth, String fileName, NodeFactory factory, int numIndepVars, Random rand) {
        this.popSize = popSize;
        this.maxDepth = maxDepth;
        this.data = new DataSet(fileName);
        this.factory = factory;
        this.numVars = numIndepVars;
        this.rand = (rand == null) ? new Random() : rand;
        this.trees = new GPTree[popSize];
        for (int i = 0; i < popSize; i++) {
            Node r = buildRandomTree(maxDepth);
            trees[i] = new GPTree(r);
        }
    }

    public void evalAll() {
        for (int i = 0; i < trees.length; i++) trees[i].evalFitness(data);
        Arrays.sort(trees);
    }

    public ArrayList<GPTree> getTopTen() {
        ArrayList<GPTree> out = new ArrayList<GPTree>();
        int k = Math.min(10, trees.length);
        for (int i = 0; i < k; i++) out.add(trees[i]);
        return out;
    }

    public void printBestTree() { if (trees.length > 0) System.out.println(trees[0]); }
    public void printBestFitness() { if (trees.length > 0) System.out.printf("%.2f%n", trees[0].getFitness()); }

    public void evolve() {
        GPTree[] next = new GPTree[popSize];
        next[0] = (GPTree) trees[0].clone();
        for (int i = 1; i < popSize; i += 2) {
            GPTree p1 = tournamentPick(4);
            GPTree p2 = tournamentPick(4);
            GPTree c1 = (GPTree) p1.clone();
            GPTree c2 = (GPTree) p2.clone();
            if (rand.nextDouble() < 0.80) GPTree.crossover(c1, c2, rand);
            if (rand.nextDouble() < 0.10) mutate(c1);
            if (rand.nextDouble() < 0.10) mutate(c2);
            next[i] = c1;
            if (i + 1 < popSize) next[i + 1] = c2;
        }
        trees = next;
    }

    private GPTree tournamentPick(int k) {
        GPTree best = null;
        for (int i = 0; i < k; i++) {
            int idx = rand.nextInt(trees.length);
            GPTree t = trees[idx];
            if (best == null || t.getFitness() < best.getFitness()) best = t;
        }
        return (GPTree) best.clone();
    }

    private void mutate(GPTree t) {
        GPTree fresh = new GPTree(buildRandomTree(1 + rand.nextInt(Math.max(1, maxDepth))));
        GPTree.crossover(t, fresh, rand);
    }

    private Node buildRandomTree(int depth) {
        if (depth <= 0) return makeLeaf();
        // ask factory; if it returns a leaf, stop; else attach children
        Node node = factory.getOperator(rand);  // may be operator or leaf
        if (node == null) return makeLeaf();
        try {
            if (node.isLeaf()) return node;
        } catch (Exception e) { /* assume operator */ }
        Node left = buildRandomTree(depth - 1);
        Node right = buildRandomTree(depth - 1);
        node.setLeft(left);
        node.setRight(right);
        return node;
    }

    private Node makeLeaf() {
        for (int tries = 0; tries < 8; tries++) {
            Node n = factory.getOperator(rand);
            if (n == null) continue;
            try {
                if (n.isLeaf()) return n;
            } catch (Exception e) { return n; }
        }
        return factory.getOperator(rand);
    }
}
