
import java.util.*;


public class TestGeneration {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        
        String file;
        if (in.hasNextLine()) {
            file = in.nextLine().trim();
        } else if (args.length > 0) {
            file = args[0].trim();
        } else {
            file = "simpleTest.csv";
        }

        DataSet ds = new DataSet(file);

      
        int numVars;
        if (in.hasNextInt()) {
            numVars = in.nextInt();
        } else if (args.length > 1) {
            numVars = Integer.parseInt(args[1]);
        } else {
            numVars = ds.getNumIndependentVariables();
        }

      
        Binop[] ops = new Binop[] {
            new Plus()
            
        };
        NodeFactory factory = new NodeFactory(ops, numVars);

        
        int popSize = 500;
        int maxDepth = 3;
        Random rand = new Random();

        Generation g = new Generation(popSize, maxDepth, file, factory, numVars, rand);
        g.evalAll();

        // produce deterministic, non-interactive output
        for (int gen = 1; gen <= 50; gen++) {
            System.out.println("Generation " + gen + ":");
            g.printBestTree();
            System.out.printf("Best fitness: %.6f%n", g.trees[0].getFitness());

            g.evolve();
            g.evalAll();
        }
    }
}
