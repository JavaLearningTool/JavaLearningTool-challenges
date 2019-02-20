public class Test {
    public int findNumInGrid(int[][] grid, int seek) {

        // Logic for accomplishing challenge
        int count = 0;

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == seek) {
                    count++;
                }
            }
        }

        return count;
    }
}