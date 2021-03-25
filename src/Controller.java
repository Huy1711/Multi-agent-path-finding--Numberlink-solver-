import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.TimeoutException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
        File file = new File("./input 2/6x6 120.in");
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
        NumberLinkResponse response = new NumberLinkResponse();
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

            for (int row = 1; row < board.length; row++) {
                List<Cell> cells = new ArrayList<>();
                for (int col = 1; col < board[row].length; col++) {
                    Cell cell = null;
                    for (int k = 0; k < model.length; k++) {
                        System.out.print(model[k] + " ");
                        if (model[k] > 0) {
                            int value = cnfConverter.getValueOfY(model[k], numberLink);
                            if (value <= 4 && value >= 1) {
//                                if (cell == null) {
//                                    cell = new Cell(row - 1, col - 1, board[row][col]);
//                                    cells.add(cell);
//                                    cell.getPattern().add(value);
//                                } else {
//                                    cell.getPattern().add(value);
//                                }
                            } else if (value > 4 && value <= CNFConverter.NUM_OF_DIRECTION + numberLink.getMaxNum()) {
                                if (cell == null) {
                                    cell = new Cell(row - 1, col - 1, board[row][col]);
                                    cells.add(cell);
                                    cell.value = value;
                                } else {
                                    cell.value = value;
                                }
                            }
                        }
                    }
                }
                response.getCells().add(cells);
            }
            System.out.println();
            printFormat(response);
        } else {
            System.out.println("UNSAT");
        }


    }

    public static void printFormat(NumberLinkResponse response) {
        for (int i = 0; i < response.getCells().size(); i++) {
            for (int j = 0; j < response.getCells().get(i).size(); j++) {

                int num = response.getCells().get(i).get(j).getValue();
                if (num <= 9) {
                    System.out.print(num + " ");
                } else {
                    System.out.print(num);
                }

//                if (response.getCells().get(i).get(j).getPattern().size() == 1) {
//                    int num = response.getCells().get(i).get(j).getValue();
//                    if (num <= 9) {
//                        System.out.print(num + " ");
//                    } else {
//                        System.out.print(num);
//                    }
//                } else if (response.getCells().get(i).get(j).getPattern().size() == 2) {
//                    int first = response.getCells().get(i).get(j).getPattern().get(0);
//                    int second = response.getCells().get(i).get(j).getPattern().get(1);
//                    if (first == CNFConverter.LEFT && second == CNFConverter.RIGHT)
//                        System.out.print("- ");
//                    else if (first == CNFConverter.LEFT && second == CNFConverter.DOWN)
//                        System.out.print("┐ ");
//                    else if (first == CNFConverter.LEFT && second == CNFConverter.UP)
//                        System.out.print("┘ ");
//                    else if (first == CNFConverter.RIGHT && second == CNFConverter.DOWN)
//                        System.out.print("┌ ");
//                    else if (first == CNFConverter.RIGHT && second == CNFConverter.UP)
//                        System.out.print("└ ");
//                    else if (first == CNFConverter.UP && second == CNFConverter.DOWN)
//                        System.out.print("│ ");
//                } else {
//                    System.out.print("* ");
//                }

            }
            System.out.println();
        }

    }
}
