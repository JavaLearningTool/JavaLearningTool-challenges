import java.util.Arrays;

public class FixMeChocolateMilkTest {

    public static void main(String[] args) {

        // Make tester for this Challenge
        FunctionReturnTester<String> tester = new FunctionReturnTester<String>(FixMeChocolateMilkTest::approved, "Test",
                "milkIsChocolate", String.class, boolean.class);
        tester.noArgsConstructor();

        if (!tester.didForm()) {
            tester.printResults();
            return;
        }

        MethodTester.useLooseStringEquality(tester);

        // test cases
        tester.addArgs(true);
        tester.addArgs(false);

        tester.setMethodInvoker((obj, arg) -> {
            boolean b = (Boolean) arg[0];
            return tester.getMethod().invoke(obj, b);
        });

        tester.setInputToStringConverter(e -> e[0] + "");

        tester.setOutputToStringConverter(Object::toString);

        // Run test cases
        tester.runTests();
    }

    public static String approved(Object[] params) {
        Object in = params[0];
        boolean input = (Boolean) in;

        if (input) {
            return "Very chocolatey";
        } else {
            return "No chocolate";
        }

    }
}