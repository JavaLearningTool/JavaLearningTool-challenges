import java.util.Arrays;

public class GridFinderTest {

    public static void main(String[] args) {

        // Make tester for this Challenge
        FunctionReturnTester<Integer> tester = new FunctionReturnTester<>(GridFinderTest::approved, // Expected method
                "Test", // Name of class
                "findNumInGrid", // Name of method being tested
                int.class, // Return type
                int[][].class, // Type of first parameter
                int.class // Type of second parameter
        );
        tester.noArgsConstructor();

        if (!tester.didForm()) {
            tester.printResults();
            return;
        }

        // Use Integer's equals to test return values from expected and actual method
        tester.setEqualityTester(Integer::equals);

        // Provide tester with a way to turn arguments into a String
        tester.setInputToStringConverter((arg) -> {
            // Parameter one is the grid
            int[][] arg1 = (int[][]) arg[0];
            // Parameter two is the number to seek
            int arg2 = (Integer) arg[1];

            return "grid: " + Arrays.deepToString(arg1) + "\tseek: " + arg2;
        });

        // Convert return value to a String
        tester.setOutputToStringConverter(out -> out + "");

        // Show the tester how to parse args and call the actual version of the method
        tester.setMethodInvoker((obj, arg) -> {
            // Parameter one is the grid
            int[][] arg1 = (int[][]) arg[0];
            // Parameter two is the number to seek
            int arg2 = (Integer) arg[1];

            // tester.getMethod() will return the actual method
            return tester.getMethod().invoke(obj, arg1, arg2);
        });

        // Even grid to use for testing
        int[][] evenArray = new int[][] { { 1, 2, 3, 4 }, { 5, 6, 7, 8 }, { 9, 1, 1, 3 } };
        // Jagged grid to use for testing
        int[][] jaggedArray = new int[][] { { 1, 2, 3, 4 }, { 5 }, { 9, 1, 1, 3, 5, 6 } };

        // test cases

        // Count the number of 1s in evenArray
        tester.addArgs(new Object[] { evenArray, 1 });

        // Count the number of 3s in evenArray
        tester.addArgs(new Object[] { evenArray, 3 });

        // Count the number of 12s in evenArray
        tester.addArgs(new Object[] { evenArray, 12 });

        // Count the number of 5s in jaggedArray
        tester.addArgs(new Object[] { jaggedArray, 5 });

        // Count the number of 4s in jaggedArray
        tester.addArgs(new Object[] { jaggedArray, 4 });

        tester.runTests();
    }

    public static int approved(Object[] args) {

        // Get parameters out of Object[]
        int[][] grid = (int[][]) args[0];

        // Must cast as Integer because you can't cast an Object to an int
        int seek = (Integer) args[1];

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