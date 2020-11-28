public class NumberLink {

    private int[][] inputs;
    private int row;
    private int col;
    private static int maxNum = 0;


    public int[][] getInputs() {
        return inputs;
    }

    public void setInputs(int[][] inputs) {
        this.inputs = inputs;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public static void setMaxNum(int maxNum) {
        NumberLink.maxNum = maxNum;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("");
        builder.append(row).append(" \t").append(col).append(" \n");
        for (int i = 1; i < inputs.length; i++) {
            for (int j = 1; j < inputs[i].length; j++) {
                builder.append(inputs[i][j]).append(" \t");
            }
            builder.append(" \n");
        }

        return builder.toString();
    }

}