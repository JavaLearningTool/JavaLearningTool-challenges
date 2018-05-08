import java.util.Arrays;

public class ArrayAverageTest {

    public static void main(String[] args) {

        // Make tester for this Challenge
        FunctionReturnTester<Double> tester = new FunctionReturnTester<Double>(ArrayAverageTest::approved, "Test",
                "findAverage", double.class, double[].class);
        tester.noArgsConstructor();

        if (!tester.didForm()) {
            tester.printResults();
            return;
        }

        // Use double equality with some leniency due to rounding
        MethodTester.useLooseDoubleEquality(tester, .1);

        // test cases
        tester.addArgs(new double[] { 98.7, 100.0, 67.5, 50.2, 89.9 });
        tester.addArgs(new double[] { 1.0 });
        tester.addArgs(new double[] { -89.0, 107.0, 8.0 });

        tester.setMethodInvoker((obj, arg) -> {
            double[] in1 = (double[]) arg[0];
            return tester.getMethod().invoke(obj, in1);
        });

        tester.setInputToStringConverter(obj -> {
            return Arrays.toString((double[]) obj[0]);
        });

        tester.setOutputToStringConverter(num -> num + "");

        // Run test cases
        tester.runTests();
    }

    public static double approved(Object[] params) {
        Object in = params[0];
        double[] input = (double[]) in;
        double sum = 0;

        for (double d : input) {
            sum += d;
        }

        return sum / input.length;
    }
}