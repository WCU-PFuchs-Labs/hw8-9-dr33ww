import java.util.Random;

public class NodeFactory {
    
    private Binop[] binops;
    
    private int numIndepVars;

    
    public NodeFactory(Binop[] ops, int numIndepVars) {
        this.binops = ops;
        this.numIndepVars = numIndepVars;
    }

    
    public Node getOperator(Random rand) {
        // decide... operator vs terminal 
        boolean chooseOp = rand.nextBoolean();

        if (chooseOp && binops != null && binops.length > 0) {
            
            int i = rand.nextInt(binops.length);
            return new Node(binops[i]); 
        } else {

            boolean chooseVar = (numIndepVars > 0) && rand.nextBoolean();
            if (chooseVar) {
                int varIndex = rand.nextInt(numIndepVars); 
                return new Node(new Variable(varIndex));
            } else {
                // simple random constant in [0,1); tweak if your project prefers a different range
                double value = rand.nextDouble();
                return new Node(new Const(value));
            }
        }
    }
}
