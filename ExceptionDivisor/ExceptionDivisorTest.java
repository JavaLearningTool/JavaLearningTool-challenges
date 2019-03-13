import java.util.Arrays;

public class ExceptionDivisorTest {

    public static void main(String[] args) {

        // Make tester for this Challenge
        FunctionReturnTester<Double> tester = new FunctionReturnTester<>(ExceptionDivisorTest::approved, "Test",
                "divide", double.class, double.class, double.class);

        tester.noArgsConstructor();
        // Shouldn't throws anything
        tester.shouldThrow();

        if (!tester.didForm()) {
            tester.printResults();
            return;
        }

        // Use Loose Double equality to test return values from expected and actual method
        MethodTester.useLooseDoubleEquality(tester, .02);

        // Provide tester with a way to turn arguments into a String
        tester.setInputToStringConverter((arg) -> {
            double a = (Double) arg[0];
            double b = (Double) arg[1];

            return "a = " + a + ", b = " + b;
        });

        // Convert return value to a String
        tester.setOutputToStringConverter(out -> out + "");

        // Show the tester how to parse args and call the actual version of the method
        tester.setMethodInvoker((obj, arg) -> {
            // Get parameters out of Object[]
            double a = (Double) arg[0];
            double b = (Double) arg[1];

            // tester.getMethod() will return the actual method
            return tester.getMethod().invoke(obj, a, b);
        });

        // Test cases
        tester.addArgs(1.0, 2.0);
        tester.addArgs(6.0, 1.0);
        tester.addArgs(1.0, 0.0);

        tester.runTests();
    }

    public static double approved(Object[] args) {

        // Get parameters out of Object[]
        double a = (Double) args[0];
        double b = (Double) args[1];

        if (b == 0) {
            throw new IllegalArgumentException("Can't divide by 0.");
        }

        return a / b;

    }
}