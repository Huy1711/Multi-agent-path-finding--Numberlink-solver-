import java.util.ArrayList;
import java.util.List;

public class CNFConverter {
    public static final int NUM_OF_DIRECTION = 4;
    public static int num_of_x = 0;
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int UP = 3;
    public static final int DOWN = 4;
    public static final int[][] DIR = new int[][] { { -1000, -1000 }, { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };
    public static int[] m_limit = new int[] { 0, 1, 0, 1, 0 };

    public SatEncoding generateSat(NumberLink numberLink) {
        num_of_x = 2 * numberLink.getCol() * (numberLink.getCol() - 1);
        m_limit[RIGHT] = numberLink.getRow();
        m_limit[DOWN] = numberLink.getCol();
        System.out.println(m_limit[RIGHT] + " " + m_limit[DOWN]);
        int[][] inputs = numberLink.getInputs();
        int variables = 0;
        int clauses = 0;
        List<String> rules = new ArrayList<>();
        for (int i = 1; i < inputs.length; i++) {
            for (int j = 1; j < inputs[i].length; j++) {

                List<String> baseRule2 = connect_same_number(i, j, numberLink);
                List<String> baseRule1 = onlyOneValue(i, j, numberLink);
                String baseRule = atLeastOneDirection(i, j, numberLink);
                rules.add(baseRule);
                clauses++;
                clauses += baseRule2.size() + baseRule1.size();

                rules.addAll(baseRule1);
                rules.addAll(baseRule2);

                // cell has number
                if (inputs[i][j] != 0) {

                    List<String> rule0 = valueFromInput(i, j, inputs[i][j], numberLink);

                    List<String> rule2 = new ArrayList<>();
                    if (isLUCornerCell(i, j)) {
                        rule2 = LUConner_exact_one_direction(i, j, numberLink);
                    } else if (isRUCornerCell(i, j)) {
                        rule2 = RUConner_exact_one_direction(i, j, numberLink);
                    } else if (isRDCornerCell(i, j)) {
                        rule2 = RDConner_exact_one_direction(i, j, numberLink);
                    } else if (isLDCornerCell(i, j)) {
                        rule2 = LDConner_exact_one_direction(i, j, numberLink);
                    } else if (isLEdgeCell(i, j)) {
                        rule2 = LEdge_exact_one_direction(i, j, numberLink);
                    } else if (isREdgeCell(i, j)) {
                        rule2 = REdge_exact_one_direction(i, j, numberLink);
                    } else if (isUEdgeCell(i, j)) {
                        rule2 = UEdge_exact_one_direction(i, j, numberLink);
                    } else if (isDEdgeCell(i, j)) {
                        rule2 = DEdge_exact_one_direction(i, j, numberLink);
                    } else {
                        rule2 = exact_one_direction(i, j, numberLink);
                    }
                    clauses += rule0.size() + rule2.size();

                    rules.addAll(rule0);
                    rules.addAll(rule2);

                // blank cell
                } else {

                    List<String> rule2 = new ArrayList<>();
                    if (isLUCornerCell(i, j)) {
                        rule2 = LUCorner_has_two_directions(i, j, numberLink);
                    } else if (isRUCornerCell(i, j)) {
                        rule2 = RUCorner_has_two_directions(i, j, numberLink);
                    } else if (isRDCornerCell(i, j)) {
                        rule2 = RDCorner_has_two_directions(i, j, numberLink);
                    } else if (isLDCornerCell(i, j)) {
                        rule2 = LDCorner_has_two_directions(i, j, numberLink);
                    } else if (isLEdgeCell(i, j)) {
                        rule2 = LEdge_has_two_directions(i, j, numberLink);
                    } else if (isREdgeCell(i, j)) {
                        rule2 = REdge_has_two_directions(i, j, numberLink);
                    } else if (isUEdgeCell(i, j)) {
                        rule2 = UEdge_has_two_directions(i, j, numberLink);
                    } else if (isDEdgeCell(i, j)) {
                        rule2 = DEdge_has_two_directions(i, j, numberLink);
                    } else {
                        rule2 = has_two_directions(i, j, numberLink);
                    }

                    clauses += rule2.size();

                    rules.addAll(rule2);
                }

            }
        }
        variables = 2*numberLink.getCol()*(numberLink.getCol() - 1) + numberLink.getRow() * numberLink.getCol() * numberLink.getMaxNum();
        System.out.println("clauses size: " + rules.size());
        System.out.println(getValueOfY(111, numberLink) - 4);
        System.out.println(getValueOfYJ(111, numberLink));
        System.out.println(getValueOfYI(111, numberLink));

        return new SatEncoding(rules, clauses, variables);
    }


    boolean isLUCornerCell(int i, int j) {
        return (i == 1 && j == 1);
    }

    boolean isLDCornerCell(int i, int j) {
        return (i == m_limit[DOWN] && j == 1);
    }

    boolean isRUCornerCell(int i, int j) {
        return (i == 1 && j == m_limit[RIGHT]);
    }

    boolean isRDCornerCell(int i, int j) {
        return (i == m_limit[DOWN] && j == m_limit[RIGHT]);
    }

    boolean isLEdgeCell(int i, int j) {
        return (j == 1 && (i > 1 && i < m_limit[DOWN]));
    }

    boolean isREdgeCell(int i, int j) {
        return (j == m_limit[RIGHT] && (i > 1 && i < m_limit[DOWN]));
    }

    boolean isDEdgeCell(int i, int j) {
        return (i == m_limit[DOWN] && (j > 1 && j < m_limit[RIGHT]));
    }

    boolean isUEdgeCell(int i, int j) {
        return (i == 1 && (j > 1 && j < m_limit[RIGHT]));
    }

    private List<String> LEdge_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "LEdge_exact_one_direction";
//        resultStringList.add(tmpClause);

        for (int k = 1; k <= NUM_OF_DIRECTION - 1; k++) {
            for (int q = k + 1; q <= NUM_OF_DIRECTION; q++) {
                if (k != LEFT && q != LEFT) {
                    String secondClause = -computePosition(i, j, k, numberLink) + " ";
                    if (q != k) {
                        secondClause += -computePosition(i, j, q, numberLink) + " ";
                        secondClause += "0";
                        resultStringList.add(secondClause);
                    }
                }
            }
        }

        return resultStringList;
    }

    private List<String> REdge_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "REdge_exact_one_direction";
//        resultStringList.add(tmpClause);

        for (int k = 1; k <= NUM_OF_DIRECTION - 1; k++) {
            for (int q = k + 1; q <= NUM_OF_DIRECTION; q++) {
                if (k != RIGHT && q != RIGHT) {
                    String secondClause = -computePosition(i, j, k, numberLink) + " ";
                    if (q != k) {
                        secondClause += -computePosition(i, j, q, numberLink) + " ";
                        secondClause += "0";
                        resultStringList.add(secondClause);
                    }
                }
            }
        }

        return resultStringList;
    }

    private List<String> DEdge_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "DEdge_exact_one_direction";
//        resultStringList.add(tmpClause);

        for (int k = 1; k <= NUM_OF_DIRECTION - 1; k++) {
            for (int q = k + 1; q <= NUM_OF_DIRECTION; q++) {
                if (k != DOWN && q != DOWN) {
                    String secondClause = -computePosition(i, j, k, numberLink) + " ";
                    if (q != k) {
                        secondClause += -computePosition(i, j, q, numberLink) + " ";
                        secondClause += "0";
                        resultStringList.add(secondClause);
                    }
                }
            }
        }

        return resultStringList;
    }

    private List<String> UEdge_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "UEdge_exact_one_direction";
//        resultStringList.add(tmpClause);

        for (int k = 1; k <= NUM_OF_DIRECTION - 1; k++) {
            for (int q = k + 1; q <= NUM_OF_DIRECTION; q++) {
                if (k != UP && q != UP) {
                    String secondClause = -computePosition(i, j, k, numberLink) + " ";
                    if (q != k) {
                        secondClause += -computePosition(i, j, q, numberLink) + " ";
                        secondClause += "0";
                        resultStringList.add(secondClause);
                    }
                }
            }
        }

        return resultStringList;
    }

    private List<String> LUConner_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "LUConner_exact_one_direction";
//        resultStringList.add(tmpClause);

        String secondClause = "";
        secondClause += -computePosition(i, j, DOWN, numberLink) + " ";
        secondClause += -computePosition(i, j, RIGHT, numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);
        return resultStringList;
    }

    private List<String> RUConner_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "RUConner_exact_one_direction";
//        resultStringList.add(tmpClause);

        String secondClause = "";
        secondClause += -computePosition(i, j, DOWN, numberLink) + " ";
        secondClause += -computePosition(i, j, LEFT, numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);
        return resultStringList;
    }

    private List<String> RDConner_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "RDConner_exact_one_direction";
//        resultStringList.add(tmpClause);

        String secondClause = "";
        secondClause += -computePosition(i, j, UP, numberLink) + " ";
        secondClause += -computePosition(i, j, LEFT, numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);
        return resultStringList;
    }

    private List<String> LDConner_exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "LDConner_exact_one_direction";
//        resultStringList.add(tmpClause);

        String secondClause = "";
        secondClause += -computePosition(i, j, UP, numberLink) + " ";
        secondClause += -computePosition(i, j, RIGHT, numberLink) + " ";
        secondClause += "0";
        resultStringList.add(secondClause);
        return resultStringList;
    }

    private List<String> LEdge_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "LEdge_has_two_directions";
//        resultStringList.add(tmpClause);

        for (int k = 1; k <= NUM_OF_DIRECTION; k++) {
            if (k != LEFT) {
                String firstClause = -computePosition(i, j, k, numberLink) + " ";
                for (int q = 1; q <= NUM_OF_DIRECTION; q++) {
                    if (q != LEFT && q != k) {
                        firstClause += computePosition(i, j, q, numberLink) + " ";
                    }
                }
                firstClause += "0";
                resultStringList.add(firstClause);
            }
        }

//        for (int k = 1; k <= NUM_OF_DIRECTION; k++) {
//            if (k != LEFT) {
//                String secondClause = computePosition(i, j, k, numberLink) + " ";
//                for (int q = 1; q <= NUM_OF_DIRECTION; q++) {
//                    if (q != LEFT && q != k) {
//                        secondClause += -computePosition(i, j, q, numberLink) + " ";
//                    }
//                }
//                secondClause += "0";
//                resultStringList.add(secondClause);
//            }
//        }
        return resultStringList;
    }

    private List<String> DEdge_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "DEdge_has_two_directions";
//        resultStringList.add(tmpClause);

        for (int k = 1; k <= NUM_OF_DIRECTION; k++) {
            if (k != DOWN) {
                String firstClause = -computePosition(i, j, k, numberLink) + " ";
                for (int q = 1; q <= NUM_OF_DIRECTION; q++) {
                    if (q != DOWN && q != k) {
                        firstClause += computePosition(i, j, q, numberLink) + " ";
                    }
                }
                firstClause += "0";
                resultStringList.add(firstClause);
            }
        }


//        for (int k = 1; k <= NUM_OF_DIRECTION; k++) {
//            if (k != DOWN) {
//                String secondClause = computePosition(i, j, k, numberLink) + " ";
//                for (int q = 1; q <= NUM_OF_DIRECTION; q++) {
//                    if (q != DOWN && q != k) {
//                        secondClause += -computePosition(i, j, q, numberLink) + " ";
//                    }
//                }
//                secondClause += "0";
//                resultStringList.add(secondClause);
//            }
//        }
        return resultStringList;
    }

    private List<String> REdge_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "REdge_has_two_directions";
//        resultStringList.add(tmpClause);

        for (int k = 1; k <= NUM_OF_DIRECTION; k++) {
            if (k != RIGHT) {
                String firstClause = -computePosition(i, j, k, numberLink) + " ";
                for (int q = 1; q <= NUM_OF_DIRECTION; q++) {
                    if (q != RIGHT && q != k) {
                        firstClause += computePosition(i, j, q, numberLink) + " ";
                    }
                }
                firstClause += "0";
                resultStringList.add(firstClause);
            }
        }


//        for (int k = 1; k <= NUM_OF_DIRECTION; k++) {
//            if (k != RIGHT) {
//                String secondClause = computePosition(i, j, k, numberLink) + " ";
//                for (int q = 1; q <= NUM_OF_DIRECTION; q++) {
//                    if (q != RIGHT && q != k) {
//                        secondClause += -computePosition(i, j, q, numberLink) + " ";
//                    }
//                }
//                secondClause += "0";
//                resultStringList.add(secondClause);
//            }
//        }
        return resultStringList;
    }

    private List<String> UEdge_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "UEdge_has_two_directions";
//        resultStringList.add(tmpClause);

        for (int k = 1; k <= NUM_OF_DIRECTION; k++) {
            if (k != UP) {
                String firstClause = -computePosition(i, j, k, numberLink) + " ";
                for (int q = 1; q <= NUM_OF_DIRECTION; q++) {
                    if (q != UP && q != k) {
                        firstClause += computePosition(i, j, q, numberLink) + " ";
                    }
                }
                firstClause += "0";
                resultStringList.add(firstClause);
            }
        }

//        for (int k = 1; k <= NUM_OF_DIRECTION; k++) {
//            if (k != UP) {
//                String secondClause = computePosition(i, j, k, numberLink) + " ";
//                for (int q = 1; q <= NUM_OF_DIRECTION; q++) {
//                    if (q != UP && q != k) {
//                        secondClause += -computePosition(i, j, q, numberLink) + " ";
//                    }
//                }
//                secondClause += "0";
//                resultStringList.add(secondClause);
//            }
//        }
        return resultStringList;
    }

    private List<String> LUCorner_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "LUCorner_has_two_directions";
//        resultStringList.add(tmpClause);

        //String first = "";
        String first = computePosition(i, j, DOWN, numberLink) + " 0";
        String second = computePosition(i, j, RIGHT, numberLink) + " 0";
        resultStringList.add(first);
        resultStringList.add(second);
        return resultStringList;
    }

    private List<String> RUCorner_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "RUCorner_has_two_directions";
//        resultStringList.add(tmpClause);

        //String first = "";
        String first = computePosition(i, j, DOWN, numberLink) + " 0";
        String second = computePosition(i, j, LEFT, numberLink) + " 0";
        resultStringList.add(first);
        resultStringList.add(second);
        return resultStringList;
    }

    private List<String> RDCorner_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "RDCorner_has_two_directions";
//        resultStringList.add(tmpClause);

        //String first = "";
        String first = computePosition(i, j, UP, numberLink) + " 0";
        String second = computePosition(i, j, LEFT, numberLink) + " 0";
        resultStringList.add(first);
        resultStringList.add(second);
        return resultStringList;
    }

    private List<String> LDCorner_has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "LDCorner_has_two_directions";
//        resultStringList.add(tmpClause);

        //String first = "";
        String first = computePosition(i, j, UP, numberLink) + " 0";
        String second = computePosition(i, j, RIGHT, numberLink) + " 0";
        resultStringList.add(first);
        resultStringList.add(second);
        return resultStringList;
    }


    // X
    private List<String> has_two_directions(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "has_two_directions";
//        resultStringList.add(tmpClause);

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

    // X Y
    private List<String> connect_same_number(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "connect_same_number";
//        resultStringList.add(tmpClause);

        int i0, j0;
        for (int k = 1; k <= NUM_OF_DIRECTION; k++) {
            i0 = DIR[k][0];
            j0 = DIR[k][1];

            if ((k == RIGHT && (j + j0) <= m_limit[k])) {

                for (int q = NUM_OF_DIRECTION + 1; q <= NUM_OF_DIRECTION + numberLink.getMaxNum(); q++) {
                    String tmpString = "";
                    // ô có giá trị 7 có kết nối sang phải -> ô bên phải có giá trị 7
                    tmpString = -computePosition(i, j, k, numberLink) + " ";
                    tmpString += -computePosition(i, j, q, numberLink) + " ";
                    tmpString += computePosition(i, j + j0, q, numberLink) + " ";
                    tmpString += "0";
                    resultStringList.add(tmpString);

                    tmpString = -computePosition(i, j, k, numberLink) + " ";
                    tmpString += -computePosition(i, j + j0, q, numberLink) + " ";
                    tmpString += computePosition(i, j, q, numberLink) + " ";
                    tmpString += "0";
                    resultStringList.add(tmpString);
                }


            } else if ((k == DOWN && i + i0 <= m_limit[k])) {

                for (int q = NUM_OF_DIRECTION + 1; q <= NUM_OF_DIRECTION + numberLink.getMaxNum(); q++) {
                    String tmpString = -computePosition(i, j, k, numberLink) + " ";
                    tmpString += -computePosition(i, j, q, numberLink) + " ";
                    tmpString += computePosition(i + i0, j, q, numberLink) + " ";
                    tmpString += "0";
                    resultStringList.add(tmpString);

                    tmpString = -computePosition(i, j, k, numberLink) + " ";
                    tmpString += -computePosition(i + i0, j, q, numberLink) + " ";
                    tmpString += computePosition(i, j, q, numberLink) + " ";
                    tmpString += "0";
                    resultStringList.add(tmpString);
                }

            }

        }
        return resultStringList;
    }

    private List<String> exact_one_direction(int i, int j, NumberLink numberLink) {
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "exact_one_direction";
//        resultStringList.add(tmpClause);

        // (x1 -> -x2) ^ (x1 -> -x3) ^ (x1 -> -x4)...
        for (int k = 1; k <= NUM_OF_DIRECTION - 1; k++) {
            for (int q = k + 1; q <= NUM_OF_DIRECTION; q++) {
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

    // Y
    private List<String> valueFromInput(int i, int j, int num, NumberLink numberLink) {
        int result = computePosition(i, j, NUM_OF_DIRECTION + num, numberLink);
        List<String> resultStringList = new ArrayList<>();
//        String tmpClause = "valueFromInput";
//        resultStringList.add(tmpClause);

        String exactNumLine = "";
        exactNumLine += result + " 0";
        resultStringList.add(exactNumLine);

        return resultStringList;
    }

    // Y
    private List<String> onlyOneValue(int i, int j, NumberLink numberLink) {
        List<String> clauseArr = new ArrayList<>();
//        String tmpClause = "onlyOneValue";
//        clauseArr.add(tmpClause);
        String exactNumLine = "";

        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            exactNumLine += computePosition(i, j, NUM_OF_DIRECTION + k, numberLink) + " ";
        }
        exactNumLine += "0";
        clauseArr.add(exactNumLine);

        for (int k = 1; k <= numberLink.getMaxNum(); k++) {
            for (int q = k + 1; q <= numberLink.getMaxNum(); q++) {
                String firstClause = -computePosition(i, j,NUM_OF_DIRECTION + k, numberLink) + " ";
                if (q != k) {
                    firstClause += -computePosition(i, j, NUM_OF_DIRECTION + q, numberLink) + " ";
                    firstClause += "0";
                    clauseArr.add(firstClause);
                }
            }
        }
        return clauseArr;
    }

    // X
    private String atLeastOneDirection(int i, int j, NumberLink numberLink) {
        String firstLine = "";
        if (isLUCornerCell(i, j)) {
            firstLine += computePosition(i, j, RIGHT, numberLink) + " ";
            firstLine += computePosition(i, j, DOWN, numberLink) + " ";
        } else if (isRUCornerCell(i, j)) {
            firstLine += computePosition(i, j, LEFT, numberLink) + " ";
            firstLine += computePosition(i, j, DOWN, numberLink) + " ";
        } else if (isRDCornerCell(i, j)) {
            firstLine += computePosition(i, j, LEFT, numberLink) + " ";
            firstLine += computePosition(i, j, UP, numberLink) + " ";
        } else if (isLDCornerCell(i, j)) {
            firstLine += computePosition(i, j, RIGHT, numberLink) + " ";
            firstLine += computePosition(i, j, UP, numberLink) + " ";
        } else if (isLEdgeCell(i, j)) {
            firstLine += computePosition(i, j, RIGHT, numberLink) + " ";
            firstLine += computePosition(i, j, UP, numberLink) + " ";
            firstLine += computePosition(i, j, DOWN, numberLink) + " ";
        } else if (isREdgeCell(i, j)) {
            firstLine += computePosition(i, j, LEFT, numberLink) + " ";
            firstLine += computePosition(i, j, UP, numberLink) + " ";
            firstLine += computePosition(i, j, DOWN, numberLink) + " ";
        } else if (isUEdgeCell(i, j)) {
            firstLine += computePosition(i, j, RIGHT, numberLink) + " ";
            firstLine += computePosition(i, j, LEFT, numberLink) + " ";
            firstLine += computePosition(i, j, DOWN, numberLink) + " ";
        } else if (isDEdgeCell(i, j)) {
            firstLine += computePosition(i, j, RIGHT, numberLink) + " ";
            firstLine += computePosition(i, j, UP, numberLink) + " ";
            firstLine += computePosition(i, j, LEFT, numberLink) + " ";
        } else {
            // x1 v x2 v x3 v x4
            firstLine += computePosition(i, j, LEFT, numberLink) + " ";
            firstLine += computePosition(i, j, RIGHT, numberLink) + " ";
            firstLine += computePosition(i, j, UP, numberLink) + " ";
            firstLine += computePosition(i, j, DOWN, numberLink) + " ";
        }
        firstLine += "0";
        return firstLine;
    }

    private int computePosition(int i, int j, int value, NumberLink numberLink) {
        int n = numberLink.getCol();
        switch (value) {
            case RIGHT:
                if (j == m_limit[RIGHT]) System.out.println("ERROR");
                return (n - 1) * (i - 1) + j;
            case LEFT:
                if (j == 1) System.out.println("ERROR");
                return (n - 1) * (i - 1) + j - 1;
            case UP:
                if (i == 1) System.out.println("ERROR");
                return (n - 1) * (j - 1) + i - 1 + n * (n - 1);
            case DOWN:
                if (i == m_limit[DOWN]) System.out.println("ERROR");
                return (n - 1) * (j - 1) + i + n * (n - 1);
            default:
                return 2 * n * (n - 1) + n * (i - 1) * numberLink.getMaxNum() + (j - 1) * numberLink.getMaxNum() + value - NUM_OF_DIRECTION;
        }
    }

    public int getValueOf(int row, int col, int positionValue, NumberLink numberLink) {
        int n = numberLink.getCol();
        if (positionValue >= 1 && positionValue <= n * (n - 1)) {
            int JValue = (positionValue - 1) % (n - 1) + 1;
            if (JValue == col) {
                return RIGHT;
            } else if (JValue + 1 == col) {
                return LEFT;
            } else return 100;
        } else if (positionValue <= 2 * n * (n - 1)) {
            int IValue = (positionValue - n * (n - 1) - 1) % (n - 1) + 1;
            if (IValue == row) {
                return DOWN;
            } else if (IValue + 1 == row) {
                return UP;
            } else return 100;
        } else {
            return (positionValue - 2 * n * (n - 1) - 1) % numberLink.getMaxNum() + 1 + 4;
//            return ((positionValue - 2 * n * (n - 1) - tmp) / numberLink.getMaxNum() + 1 - col) / n + 1;
        }
    }


    public int getValueOfY(int positionValue, NumberLink numberLink) {
        int n = numberLink.getCol();
        return (positionValue - num_of_x - 1) % numberLink.getMaxNum() + 1 + NUM_OF_DIRECTION;
    }

    public int getValueOfYJ(int positionValue, NumberLink numberLink) {
        return ((positionValue - num_of_x - 1)/numberLink.getMaxNum()) % numberLink.getCol() + 1;
    }

    public int getValueOfYI(int positionValue, NumberLink numberLink) {
        return (positionValue - num_of_x - 1) / (numberLink.getMaxNum() * numberLink.getCol()) + 1;
    }
}