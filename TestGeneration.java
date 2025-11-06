
import java.util.*;

public class TestGeneration {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter data file: ");
        String file = in.nextLine().trim();

        System.out.print("Enter number of independent variables: ");
        int numVars = Integer.parseInt(in.nextLine().trim());

        // Use NodeFactory with null ops (factory will generate leaves when needed).
        NodeFactory factory = new NodeFactory(null, numVars);

        Generation g = new Generation(500, 3, file, factory, numVars, new Random());
        g.evalAll();

        // best tree
        g.printBestTree();

        // top ten fitness with exactly 2 decimals, comma-separated
        ArrayList<GPTree> top = g.getTopTen();
        System.out.println("Top Ten Fitness Values:");
        for (int i = 0; i < top.size(); i++) {
            System.out.printf("%.2f", top.get(i).getFitness());
            if (i < top.size() - 1) System.out.print(", ");
        }
        System.out.println();
    }
}
