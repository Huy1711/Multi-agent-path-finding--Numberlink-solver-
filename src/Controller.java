import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Controller {
    private static CNFConverter cnfConverter = new CNFConverter();
    private static SATSolver satSolver;
    public static int rows;
    public static int cols;

    public static void main(String[] args) throws IOException, TimeoutException, ParseFormatException, ContradictionException {
        // Doc tu file Text
        File file = new File("./input 2/9x9 120.in");
        Scanner sc = new Scanner(file);
        NumberLink numberLink = new NumberLink();
        numberLink.setRow(sc.nextInt());
        rows = numberLink.getRow();
        numberLink.setCol(sc.nextInt());
        cols = numberLink.getCol();
        NumberLink.setMaxNum(sc.nextInt());
        int[][] input = new int[numberLink.getRow() + 1][numberLink.getCol() + 1];
        for (int i = 1; i < numberLink.getRow() + 1; i++) {
            for (int j = 1; j < numberLink.getCol() + 1; j++) {
//                if (input[i][j] == 0) {
//                
//                    sc.nextInt();
//                }
                input[i][j] = sc.nextInt();
            }
        }
        numberLink.setInputs(input);

        System.out.println(numberLink);


        // Ghi ra file CNF
        File fileCNF = new File("text.cnf");
        FileWriter writer = new FileWriter(fileCNF);

        SatEncoding satEncoding = cnfConverter.generateSat(numberLink);
        String firstLine = "p cnf " + satEncoding.getVariables() + " " + satEncoding.getClauses();
        writer.write(firstLine + "\n");
        List<String> rules = satEncoding.getRules();
        for (int i = 0; i < rules.size(); i++) {
            // dong cuoi khong xuong dong
            if (i == rules.size() - 1) {
                writer.write(rules.get(i));
                continue;
            }
            writer.write(rules.get(i) + "\n");
        }
        writer.flush();
        writer.close();

        // SAT Solve
        DimacsReader reader = new DimacsReader(SolverFactory.newDefault());
        reader.parseInstance("text.cnf");
        satSolver = new SATSolver(reader);


        IProblem problem = satSolver.solve("text.cnf");


        long t1 = System.currentTimeMillis();
        if (problem.isSatisfiable()) {
            System.out.println("SAT");
            long t2 = System.currentTimeMillis();
            System.out.println("Total time: " + (t2 - t1) + "ms.\n");
            System.out.println("Var: " + problem.nVars());
            System.out.println("Constraint: " + problem.nConstraints());



            int[] model = problem.model();
            int[][] board = numberLink.getInputs();


            int countBreak = 0;
                for (int k = CNFConverter.num_of_x; k < model.length; k++) {

                    if (model[k] > 0) {

                        int positionValue = model[k];
                        int i = cnfConverter.getValueOfYI(positionValue, numberLink);
                        int j = cnfConverter.getValueOfYJ(positionValue, numberLink);

                        int breakPoint = (i - 1) % numberLink.getCol();
                        int value = cnfConverter.getValueOfY(model[k], numberLink);

                        if (breakPoint == countBreak) {
                            System.out.println();
                            countBreak++;
                        }

                        System.out.print((value - 4) + " ");
                    }


            }

        } else {
            System.out.println("UNSAT");
            System.out.println("Var: " + problem.nVars());

        }


    }

}
