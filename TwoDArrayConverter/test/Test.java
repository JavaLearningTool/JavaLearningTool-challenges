public class Test {

    public int[][] to2DArray(int[] input) {
        int[][] ret = new int[(int) Math.ceil(input.length / 5.0)][5];

        for (int i = 0; i < ret.length; i++) {
            for (int j = 0; j < 5; j++) {
                int index = i * 5 + j;
                if (index >= input.length) {
                    break;
                }
                ret[i][j] = input[index];
            }
        }

        return ret;
    }
}