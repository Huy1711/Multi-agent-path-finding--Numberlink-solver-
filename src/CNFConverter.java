import java.util.ArrayList;
import java.util.List;

public class CNFConverter {
    public static final int NUM_OF_DIRECTION = 4;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int UP = 3;
    public static final int DOWN = 4;
    public static final int[][] DIR = new int[][] { { -1000, -1000 }, { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };
    public static final int[] m_limit = new int[] { 0, 1, 10, 1, 10 };

    public SatEncoding generateSat(NumberLink numberLink) {
        int[][] inputs = numberLink.getInputs();
        int variables = 0;
        int clauses = 0;
        List<String> rules = new ArrayList<>();
        for (int i = 1; i < inputs.length; i++) {
            for (int j = 1; j < inputs[i].length; j++) {
                // cell has number
                if (inputs[i][j] != 0) {
                    String baseRule = atLeastOneDirection(i, j, numberLink);
                    rules.add(baseRule);
                    clauses++;

                    List<String> rule2 = exact_one_direction(i, j, numberLink);
                    List<String> rule1 = connectToNumCell(i, j, inputs[i][j], numberLink);
                    List<String> rule3 = connect_same_number(i, j, numberLink);

                    clauses += rule1.size() + rule2.size() + rule3.size();

                    rules.addAll(rule2);
                    rules.addAll(rule1);
                    rules.addAll(rule3);
                } else {

                    List<String> rule1 = has_two_directions(i, j, numberLink);
                    List<String> rule2 = connect_same_number(i, j, numberLink);
                    List<String> rule3 = limit_boundary(i, j, numberLink);

                    clauses += rule1.size() + rule2.size() + rule3.size();

                    rules.addAll(rule1);
                    rules.addAll(rule3);
                    rules.addAll(rule2);

                }

            }
        }
        variables = numberLink.getRow() * numberLink.getCol() * (NUM_OF_DIRECTION + numberLink.getMaxNum() + numberLink.getRow() * numberLink.getCol());
        return new SatEncoding(rules, clauses, variables);
    }

    private List<String> limit_boundary(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();

        if (j <= 1) {
            resultStringList.add(-computePosition(i, j, LEFT, numberLink) + " 0");
        }
        if (j >= m_limit[RIGHT] - 1) {
            resultStringList.add(-computePosition(i, j, RIGHT, numberLink) + " 0");
        }
        if (i <= 1) {
            resultStringList.add(-computePosition(i, j, UP, numberLink) + " 0");
        }
        if (i >= m_limit[DOWN] - 1) {
            resultStringList.add(-computePosition(i, j, DOWN, numberLink) + " 0");
        }

        return resultStringList;
    }

    private List<String> has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();

        // x1 -> (x2 v x3 v x4)
        for (int k = 1; k <= NUM_OF_DIRECTION; k++) {
            String firstClause = -computePosition(i, j, k, numberLink) + " ";
            for (int q = 1; q <= NUM_OF_DIRECTION; q++) {
                if (q != k) {
                    firstClause += computePosition(i, j, q, numberLink) + " ";
                }
            }
            firstClause += "0";
            resultStringList.add(firstClause);
        }
        // -x1 -> (-x2 v -x3 v -x4)
        for (int k = 1; k <= NUM_OF_DIRECTION; k++) {
            String second = computePosition(i, j, k, numberLink) + " ";
            for (int q = 1; q <= NUM_OF_DIRECTION; q++) {
                if (q != k) {
                    second += -computePosition(i, j, q, numberLink) + " ";
                }
            }
            second += "0";
            resultStringList.add(second);
        }

        return resultStringList;
    }

    private List<String> connect_same_number(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();

        int i0, j0;
        String atleastOneDirection;
        for (int k = 1; k <= NUM_OF_DIRECTION; k++) {
            i0 = DIR[k][0];
            j0 = DIR[k][1];

            if ((k == RIGHT && (j + j0) <= m_limit[k]) || (k == LEFT && (j + j0) >= m_limit[k])) {
                // ô có kết nối sang trái kéo theo ô ngay bên trái có kết nối sang phải
                atleastOneDirection = (-computePosition(i, j, k, numberLink)) + " ";
                switch (k) {
                    case LEFT:
                        atleastOneDirection += computePosition(i + i0, j + j0, RIGHT, numberLink) + " ";
                        break;
                    case RIGHT:
                        atleastOneDirection += computePosition(i + i0, j + j0, LEFT, numberLink) + " ";
                        break;
                }
                atleastOneDirection += "0";
                resultStringList.add(atleastOneDirection);

                for (int q = NUM_OF_DIRECTION + 1; q <= NUM_OF_DIRECTION + numberLink.getMaxNum(); q++) {
                    String tmpString = "";
                    // ô có giá trị 7 có kết nối sang phải -> ô bên phải có giá trị 7
                    tmpString = -computePosition(i, j, k, numberLink) + " ";
                    tmpString += -computePosition(i, j, q, numberLink) + " ";
                    tmpString += computePosition(i, j + j0, q, numberLink) + " ";
                    tmpString += "0";
                    resultStringList.add(tmpString);
                    // ô không có giá trị 7 có kết nối sang phải -> ô bên phải không có giá trị 7
                    tmpString = -computePosition(i, j, k, numberLink) + " ";
                    tmpString += computePosition(i, j, q, numberLink) + " ";
                    tmpString += -computePosition(i, j + j0, q, numberLink) + " ";
                    tmpString += "0";
                    resultStringList.add(tmpString);
                }
            } else if ((k == DOWN && i + i0 <= m_limit[k]) || (k == UP && i + i0 >= m_limit[k])) {
                atleastOneDirection = (-computePosition(i, j, k, numberLink)) + " ";
                switch (k) {
                    case UP:
                        atleastOneDirection += computePosition(i + i0, j + j0, DOWN, numberLink) + " ";
                        break;
                    case DOWN:
                        atleastOneDirection += computePosition(i + i0, j + j0, UP, numberLink) + " ";
                        break;
                }
                atleastOneDirection += "0";
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

        // (x1 -> -x2) ^ (x1 -> -x3) ^ (x1 -> -x4)...
        for (int k = 1; k <= NUM_OF_DIRECTION; k++) {
            for (int q = 1; q <= NUM_OF_DIRECTION; q++) {
                String firstClause = -computePosition(i, j, k, numberLink) + " ";
                if (q != k) {
                    firstClause += -computePosition(i, j, q, numberLink) + " ";
                    firstClause += "0";
                    resultStringList.add(firstClause);
                }
            }
        }

        return resultStringList;
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
        // x1 v x2 v x3 v x4
        String firstLine = "";
        firstLine += calculatePosition(i, j, LEFT, numberLink, true);
        firstLine += calculatePosition(i, j, RIGHT, numberLink, true);
        firstLine += calculatePosition(i, j, UP, numberLink, true);
        firstLine += calculatePosition(i, j, DOWN, numberLink, true);
        firstLine += "0";
        return firstLine;
    }

    private int computePosition(int i, int j, int value, NumberLink numberLink) {
        return (i - 1) * (NUM_OF_DIRECTION + numberLink.getMaxNum()) * numberLink.getCol()
                + (j - 1) * (NUM_OF_DIRECTION + numberLink.getMaxNum()) + value;
    }

    private String calculatePosition(int i, int j, int value, NumberLink numberLink, boolean positive) {
        int sign = positive ? 1 : -1;
        if (value == LEFT) {
            if (j > 1) {
                return sign * computePosition(i, j, LEFT, numberLink) + " ";
            } else return "";
        } else if (value == RIGHT) {
            if (j < m_limit[RIGHT]) {
                return sign * computePosition(i, j, RIGHT, numberLink) + " ";
            } else return "";
        } else if (value == UP) {
            if (i > 1) {
                return sign * computePosition(i, j, UP, numberLink) + " ";
            } else return "";
        } else if (value == DOWN) {
            if (i < m_limit[DOWN]) {
                return sign * computePosition(i, j, DOWN, numberLink) + " ";
            } else return "";
        }
        return "";
    }

    public int getValueOf(int row, int col, int positionValue, NumberLink numberLink) {
        return positionValue - (row - 1) * (NUM_OF_DIRECTION + numberLink.getMaxNum()) * numberLink.getCol() -
                (col - 1) * (NUM_OF_DIRECTION + numberLink.getMaxNum());
    }
}
