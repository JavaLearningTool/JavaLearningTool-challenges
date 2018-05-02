import java.util.Arrays;

public class TwoDArrayConverterTest {

    public static void main(String[] args) {

        // Make tester for this Challenge
        FunctionReturnTester<int[][]> tester = new FunctionReturnTester<int[][]>(TwoDArrayConverterTest::approved,
                "Test", "to2DArray", int[][].class, int[].class);
        tester.noArgsConstructor();

        if (!tester.didForm()) {
            tester.printResults();
            return;
        }

        // Use arrays's deep equals method
        tester.setEqualityTester(Arrays::deepEquals);

        // Test Cases
        tester.addArgs((Object) new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        tester.addArgs((Object) new int[] { 11, -2, 3 });
        tester.addArgs((Object) new int[] {});
        tester.addArgs((Object) new int[] { 0, 11, 77, -14, 8, 8 });

        tester.setMethodInvoker((obj, arg) -> {
            int[] in1 = (int[]) arg[0];
            return tester.getMethod().invoke(obj, in1);
        });

        tester.setInputToStringConverter(arg -> {
            int[] in1 = (int[]) arg[0];
            return Arrays.toString(in1);
        });

        tester.setOutputToStringConverter(Arrays::deepToString);

        // Run test cases
        tester.runTests();
    }

    public static int[][] approved(Object[] arg) {
        int[] input = (int[]) arg[0];
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