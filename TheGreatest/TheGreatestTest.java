import java.util.Arrays;

public class TheGreatestTest {

    public static void main(String[] args) {

        // Make tester for this Challenge
        FunctionReturnTester<Integer> tester = new FunctionReturnTester<Integer>(TheGreatestTest::approved, "Test",
                "findGreater", int.class, int.class, int.class);
        tester.noArgsConstructor();

        if (!tester.didForm()) {
            tester.printResults();
            return;
        }

        tester.setEqualityTester(Integer::equals);

        tester.setInputToStringConverter((obj) -> {
            int in1 = (Integer) obj[0];
            int in2 = (Integer) obj[1];
            return "a: " + in1 + ", b: " + in2;
        });

        tester.setOutputToStringConverter(out -> out + "");

        tester.setMethodInvoker((obj, arg) -> {
            int in1 = (int) (Integer) arg[0];
            int in2 = (int) (Integer) arg[1];
            return tester.getMethod().invoke(obj, in1, in2);
        });

        // test cases
        tester.addArgs(100, 99);
        tester.addArgs(50, 50);
        tester.addArgs(0, 75);
        tester.addArgs(-1, -70);

        tester.runTests();
    }

    public static int approved(Object[] params) {
        int a = (Integer) params[0];
        int b = (Integer) params[1];
        return (a > b ? a : b);
    }
}