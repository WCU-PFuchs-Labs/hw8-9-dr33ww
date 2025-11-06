
import java.util.*;
import tabular.*;

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
        Arrays.sort(trees); // ascending fitness
    }

    public ArrayList<GPTree> getTopTen() {
        ArrayList<GPTree> out = new ArrayList<GPTree>();
        int k = Math.min(10, trees.length);
        for (int i = 0; i < k; i++) out.add(trees[i]);
        return out;
    }

    public void printBestTree() {
        if (trees.length > 0) System.out.println(trees[0]);
    }

    public void printBestFitness() {
        if (trees.length > 0) System.out.printf("%.2f%n", trees[0].getFitness());
    }

    public void evolve() {
        GPTree[] next = new GPTree[popSize];
        int elite = Math.max(2, popSize / 10);
        // carry over 1 elite unchanged
        next[0] = (GPTree) trees[0].clone();

        for (int i = 1; i < popSize; i += 2) {
            GPTree p1 = tournamentPick(4);
            GPTree p2 = tournamentPick(4);
            GPTree c1 = (GPTree) p1.clone();
            GPTree c2 = (GPTree) p2.clone();

            // 80% crossover rate
            if (rand.nextDouble() < 0.80) {
                GPTree.crossover(c1, c2, rand);
            }

            // small mutation: with low prob, replace a random subtree with a fresh random subtree
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
        // replace a random subtree with a new random subtree
        // reuse GPTree internals to select random node; simple approach: crossover with a tiny fresh tree
        GPTree fresh = new GPTree(buildRandomTree(1 + rand.nextInt(Math.max(1, maxDepth))));
        GPTree.crossover(t, fresh, rand);
    }

    // -------- random tree builder using NodeFactory + Variable/Const ----------
    private Node buildRandomTree(int depth) {
        if (depth <= 0) {
            return makeLeaf();
        }
        // pick either operator or leaf with some chance to keep trees short
        boolean makeOp = rand.nextDouble() < 0.75; // bias toward internal nodes at higher depth
        if (!makeOp) return makeLeaf();

        Node opNode = factory.getOperator(rand);  // returns a Node that wraps a Binop
        Node left = buildRandomTree(depth - 1);
        Node right = buildRandomTree(depth - 1);
        opNode.setLeft(left);
        opNode.setRight(right);
        return opNode;
    }

    private Node makeLeaf() {
        boolean chooseVar = (numVars > 0) && rand.nextBoolean();
        if (chooseVar) {
            int varIndex = rand.nextInt(numVars);
            return new Node(new Variable(varIndex));
        } else {
            double value = rand.nextDouble();
            return new Node(new Const(value));
        }
    }
}
