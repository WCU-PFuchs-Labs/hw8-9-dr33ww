
import java.util.*;
import tabular.*;

public class TestGP {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter data file: ");
        String file = in.nextLine().trim();

        System.out.print("Enter number of independent variables: ");
        int numVars = Integer.parseInt(in.nextLine().trim());

        Binop[] ops = new Binop[] { new Plus(), new Minus(), new Multi(), new Divide() };
        NodeFactory factory = new NodeFactory(ops, numVars);

        Generation g = new Generation(500, 3, file, factory, numVars, new Random());
        g.evalAll();

        for (int gen = 1; gen <= 50; gen++) {
            System.out.println("Generation " + gen + ":");
            g.printBestTree();
            System.out.printf("Best fitness: %.6f%n", g.trees[0].getFitness());

            g.evolve();
            g.evalAll();
        }
    }
}
