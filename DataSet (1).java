

import java.util.*;
import java.io.*;

public class DataSet {
    private ArrayList<DataRow> data;
    private int numIndepVariables;
//i am radiant
    public DataSet(String fileName) {
        data = new ArrayList<>();

        try (Scanner sc = new Scanner(new File(fileName))) {
            if (!sc.hasNextLine()) return;

            // read the toptop to determine number of independent variables
            String header = sc.nextLine();
            String[] headers = header.split(",");
            numIndepVariables = headers.length - 1;

            // read row
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length < headers.length) continue;

                // first column is y (dependent)
                double y = Double.parseDouble(parts[0].trim());

                // remaining columns are independent variables
                double[] x = new double[numIndepVariables];
                for (int i = 0; i < numIndepVariables; i++) {
                    x[i] = Double.parseDouble(parts[i + 1].trim());
                }

                data.add(new DataRow(y, x));
            }
        } catch (Exception e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public ArrayList<DataRow> getRows() {
        return data;
    }

 
    public int getNumIndependentVariables() {
        return numIndepVariables;
    }
}
