package tabular;
//man idek

public class DataRow {
    private double y;        // dependent variable
    private double[] x;      // independent variables

    public DataRow(double y, double[] x) {
        this.y = y;          // store the dependent variable
        this.x = x;          // store the independent variable array
    }

    public double getDependentVariable() {
        return y;
    }

    public double[] getIndependentVariables() {
        return x;
    }
}

