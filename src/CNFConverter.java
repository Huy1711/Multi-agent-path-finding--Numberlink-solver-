import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CNFConverter {
    public static final int NUM_OF_DIRECTION = 4;
    public static final int MAX_PERMUTATION_SIZE = 100;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int UP = 3;
    public static final int DOWN = 4;
    public static final int[][] DIR = new int[][] { { -1000, -1000 }, { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };
    public static final int[] m_limit = new int[] { 0, 1, 10, 1, 10 };
    public boolean[] m_mark = new boolean[MAX_PERMUTATION_SIZE];

    public SatEncoding generateSat(NumberLink numberLink) {
        int[][] inputs = numberLink.getInputs();
        int variables = 0;
        int clauses = 0;
        List<String> rules = new ArrayList<>();
        for (int i = 1; i < inputs.length; i++) {
            for (int j = 1; j < inputs[i].length; j++) {
                // Ko la blank cell
                if (inputs[i][j] != 0) {
                    String baseRule = atLeastOneDirection(i, j, numberLink);
                    rules.add(baseRule);
                    clauses++;

                    List<String> rule1 = connectToNumCell(i, j, inputs[i][j], numberLink);
                    List<String> rule2 = exact_one_direction(i, j, numberLink);
                    List<String> rule3 = connect_same_number(i, j, numberLink);

                    clauses += rule1.size() + rule2.size() + rule3.size();

                    rules.addAll(rule1);
                    rules.addAll(rule2);
                    rules.addAll(rule3);
                } else {
                    List<String> rule1 = maybe_two_direction(i, j, numberLink);
                    List<String> rule2 = connect_same_number(i, j, numberLink);
                    List<String> rule3 = limit_boundary(i, j, numberLink);
                    List<String> rule4 = maybeBlankCell(i, j, numberLink);
                    List<String> rule5 = maybe_one_number(i, j, numberLink);

                    clauses += rule1.size() + rule2.size() + rule3.size() + rule4.size() + rule5.size();

                    rules.addAll(rule1);
                    rules.addAll(rule2);
                    rules.addAll(rule3);
                    rules.addAll(rule4);
                    rules.addAll(rule5);
                }

            }
        }
        variables = numberLink.getRow() * numberLink.getCol() * (NUM_OF_DIRECTION + numberLink.getMaxNum());
        return new SatEncoding(rules, clauses, variables);
    }

    private List<String> maybe_one_number(int i, int j, NumberLink numberLink) {
        List<String> resulStringList = new ArrayList<>();

        String atleastNum = "";
        for(int k = 1; k <= numberLink.getMaxNum() ; k++){
            atleastNum += computePosition(i,j,NUM_OF_DIRECTION + k, numberLink) + " ";
        }

        String curRule = "";
        for(int q = 1; q <= NUM_OF_DIRECTION; q++){
            curRule = "" + -computePosition(i,j,q, numberLink);
            resulStringList.add(curRule + " " + atleastNum + "0");
        }
        return resulStringList;
    }

    private List<String> maybeBlankCell(int i, int j, NumberLink numberLink) {
        List<String> resulStringList = new ArrayList<>();
        String firstLine = "";
        firstLine += computePosition(i,j,LEFT, numberLink) + " " +
                computePosition(i,j,RIGHT, numberLink)  + " " +
                computePosition(i,j,UP, numberLink)  + " " +
                computePosition(i,j,DOWN, numberLink);
        for(int k = 1; k <= numberLink.getMaxNum(); k++){
            resulStringList.add(firstLine + " " + (-computePosition(i,j,NUM_OF_DIRECTION + k, numberLink)) + " 0");
        }
        return resulStringList;
    }

    private List<String> limit_boundary(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();

        if (j <= 1) {
            resultStringList.add(-computePosition(i, j, LEFT, numberLink) + " 0");
        }
        if (j >= m_limit[RIGHT]) {
            resultStringList.add(-computePosition(i, j, RIGHT, numberLink) + " 0");
        }
        if (i <= 1) {
            resultStringList.add(-computePosition(i, j, UP, numberLink) + " 0");
        }
        if (i >= m_limit[DOWN]) {
            resultStringList.add(-computePosition(i, j, DOWN, numberLink) + " 0");
        }

        return resultStringList;
    }

    private List<String> maybe_two_direction(int i, int j, NumberLink numberLink) {
        List<String> resulStringList = new ArrayList<>();

        for (int k = 1; k <= NUM_OF_DIRECTION; k++) {
            String firstClause = -computePosition(i, j, k, numberLink) + " ";
            for (int q = 1; q <= NUM_OF_DIRECTION; q++) {
                if (q != k) {
                    firstClause += computePosition(i, j, q, numberLink) + " ";
                }
            }
            firstClause += "0";
            resulStringList.add(firstClause);

            initPermutation();
            Set<Set<Integer>> permutationResult = generatePermutation(NUM_OF_DIRECTION, 3, 1, 0);
            for (Set<Integer> values : permutationResult) {
                if (!values.contains(k)) {
                    continue;
                }
                String tmpString = "";
                for (Integer value : values) {
                    tmpString += -computePosition(i, j, value, numberLink) + " ";
                }
                tmpString += "0";
                resulStringList.add(tmpString);
            }
        }
        return resulStringList;
    }

    private List<String> connect_same_number(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();

        int i0, j0;
        String atleastOneDirection;
        for (int k = 1; k <= NUM_OF_DIRECTION; k++) {
            i0 = DIR[k][0];
            j0 = DIR[k][1];

            atleastOneDirection = (-computePosition(i, j, k, numberLink)) + " ";
            switch (k) {
                case LEFT:
                    atleastOneDirection += computePosition(i + i0, j + j0, RIGHT, numberLink) + " ";
                    break;
                case RIGHT:
                    atleastOneDirection += computePosition(i + i0, j + j0, LEFT, numberLink) + " ";
                    break;
                case UP:
                    atleastOneDirection += computePosition(i + i0, j + j0, DOWN, numberLink) + " ";
                    break;
                case DOWN:
                    atleastOneDirection += computePosition(i + i0, j + j0, UP, numberLink) + " ";
                    break;
            }
            atleastOneDirection += "0";

            if ((k == RIGHT && (j + j0) <= m_limit[k]) || (k == LEFT && (j + j0) >= m_limit[k])) {
                resultStringList.add(atleastOneDirection);
                for (int q = NUM_OF_DIRECTION + 1; q <= NUM_OF_DIRECTION + numberLink.getMaxNum(); q++) {
                    String tmpString = "";
                    tmpString = -computePosition(i, j, k, numberLink) + " ";
                    tmpString += -computePosition(i, j, q, numberLink) + " ";
                    tmpString += computePosition(i, j + j0, q, numberLink) + " ";
                    tmpString += "0";
                    resultStringList.add(tmpString);

                    tmpString = -computePosition(i, j, k, numberLink) + " ";
                    tmpString += computePosition(i, j, q, numberLink) + " ";
                    tmpString += -computePosition(i, j + j0, q, numberLink) + " ";
                    tmpString += "0";
                    resultStringList.add(tmpString);
                }
            } else if ((k == DOWN && i + i0 <= m_limit[k]) || (k == UP && i + i0 >= m_limit[k])) {
                resultStringList.add(atleastOneDirection);
                for (int q = NUM_OF_DIRECTION + 1; q <= NUM_OF_DIRECTION + numberLink.getMaxNum(); q++) {
                    String tmpString = -computePosition(i, j, k, numberLink) + " ";
                    tmpString += -computePosition(i, j, q, numberLink) + " ";
                    tmpString += computePosition(i + i0, j, q, numberLink) + " ";
                    tmpString += "0";
                    resultStringList.add(tmpString);

                    tmpString = -computePosition(i, j, k, numberLink) + " ";
                    tmpString += computePosition(i, j, q, numberLink) + " ";
                    tmpString += -computePosition(i + i0, j, q, numberLink) + " ";
                    tmpString += "0";
                    resultStringList.add(tmpString);
                }
            }
        }
        return resultStringList;
    }

    private List<String> exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();

        String firstLine = "";
        firstLine += (j > 1) ? computePosition(i, j, LEFT, numberLink) + " " : "";
        firstLine += (j < m_limit[RIGHT]) ? computePosition(i, j, RIGHT, numberLink) + " " : "";
        firstLine += (i > 1) ? computePosition(i, j, UP, numberLink) + " " : "";
        firstLine += (i < m_limit[DOWN]) ? computePosition(i, j, DOWN, numberLink) + " " : "";
        firstLine += "0";
        resultStringList.add(firstLine);

        initPermutation();
        Set<Set<Integer>> permutationResult = generatePermutation(NUM_OF_DIRECTION, 2, 1, 0);

        for (Set<Integer> values : permutationResult) {
            String tmpString = "";
            for (Integer value : values) {
                tmpString += -computePosition(i, j, value, numberLink) + " ";
            }
            tmpString += "0";
            resultStringList.add(tmpString);
        }

        return resultStringList;
    }

    private Set<Set<Integer>> generatePermutation(int num, int base, int step, int prev) {
        Set<Set<Integer>> permutationResult = new HashSet<>();
        if (step >= base + 1) {
            Set<Integer> tmpSet = new HashSet<>();
            for (int i = 1; i <= num; i++) {
                if (m_mark[i]) {
                    tmpSet.add(i);
                }
            }

            permutationResult.add(tmpSet);
            return permutationResult;
        }

        for (int i = 1; i <= num; i++) {
            if (m_mark[i] == false && i > prev) {
                m_mark[i] = true;
                generatePermutation(num, base, step + 1, i);
                m_mark[i] = false;
            }
        }

        return permutationResult;
    }

    private void initPermutation() {
        for (int i = 0; i < MAX_PERMUTATION_SIZE; i++) {
            m_mark[i] = false;
        }
    }

    private List<String> connectToNumCell(int i, int j, int num, NumberLink numberLink) {
        int result = computePosition(i, j, NUM_OF_DIRECTION + num, numberLink);
        List<String> resultStringList = new ArrayList<>();

        String exactNumLine = "";
        exactNumLine += result + " 0";
        resultStringList.add(exactNumLine);

        for (int k = 1; k < numberLink.getMaxNum(); k++) {
            if (k != num) {
                exactNumLine = -computePosition(i, j, NUM_OF_DIRECTION + k, numberLink) + " 0";
                resultStringList.add(exactNumLine);
            }
        }
        return resultStringList;
    }

    private String atLeastOneDirection(int i, int j, NumberLink numberLink) {
        String rule = "";
        rule += (j >= 1) ? computePosition(i, j, LEFT, numberLink) + " " : "";
        rule += (j < m_limit[RIGHT]) ? computePosition(i, j, RIGHT, numberLink) + " " : "";
        rule += (i >= 1) ? computePosition(i, j, UP, numberLink) + " " : "";
        rule += (i < m_limit[DOWN]) ? computePosition(i, j, DOWN, numberLink) + " " : "";
        rule += "0";
        return rule;
    }

    private int computePosition(int i, int j, int value, NumberLink numberLink) {
        return (i - 1) * (NUM_OF_DIRECTION + numberLink.getMaxNum()) * numberLink.getCol()
                + (j - 1) * (NUM_OF_DIRECTION + numberLink.getMaxNum()) + value;
    }

    public int getValueOf(int row, int col, int positionValue, NumberLink numberLink) {
        return positionValue - (row - 1) * (NUM_OF_DIRECTION + numberLink.getMaxNum()) * numberLink.getCol() -
                (col - 1) * (NUM_OF_DIRECTION + numberLink.getMaxNum());
    }
}
