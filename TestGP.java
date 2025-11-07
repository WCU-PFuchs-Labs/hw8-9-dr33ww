
import java.util.*;


public class TestGP {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

      
        String file;
        if (in.hasNextLine()) {
            file = in.nextLine().trim();          // read from stdin if provided
        } else if (args.length > 0) {
            file = args[0].trim();                 // or first arg
        } else {
            file = "simpleTest.csv";               // fallback default
        }

        DataSet ds = new DataSet(file);

        // number of independent variables
        int numVars;
        if (in.hasNextInt()) {
            numVars = in.nextInt();
        } else if (args.length > 1) {
            numVars = Integer.parseInt(args[1]);
        } else {
            numVars = ds.getNumIndependentVariables(); // safest default
        }

      
        Binop[] ops = new Binop[] {
            new Plus()
         
        };
        NodeFactory factory = new NodeFactory(ops, numVars);

        Generation g = new Generation(500, 3, file, factory, numVars, new Random());
        g.evalAll();

        for (int gen = 1; gen <= 50; gen++) {
            // DO NOT print interactive prompts; just deterministic output
            System.out.println("Generation " + gen + ":");
            g.printBestTree();
            System.out.printf("Best fitness: %.6f%n", g.trees[0].getFitness());

            g.evolve();
            g.evalAll();
        }
    }
}
