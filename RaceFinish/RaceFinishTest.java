import java.util.Arrays;

public class RaceFinishTest {

    public static void main(String[] args) {

        // Make tester for this Challenge
        FunctionReturnTester<String> tester = new FunctionReturnTester<String>(RaceFinishTest::approved, "Test",
                "raceFinish", String.class, String[].class);
        tester.noArgsConstructor();

        if (!tester.didForm()) {
            tester.printResults();
            return;
        }

        MethodTester.useLooseStringEquality(tester);

        // test cases
        tester.addArgs((Object) new String[] { "Chad", "Mike", "Brad", "Carl", "Tim", "Steve" });
        tester.addArgs((Object) new String[] { "Megan", "Stacy", "Stacy's Mom" });
        tester.addArgs((Object) new String[] { "Steve" });

        tester.setMethodInvoker((obj, arg) -> {
            String[] strs = (String[]) arg[0];
            return tester.getMethod().invoke(obj, (Object) strs);
        });

        tester.setInputToStringConverter(e -> Arrays.toString((String[]) e[0]));

        tester.setOutputToStringConverter(Object::toString);

        // Run test cases
        tester.runTests();
    }

    public static String approved(Object[] params) {
        Object in = params[0];
        String[] input = (String[]) in;

        String ret = "";

        for (int i = 0; i < input.length; i++) {
            ret += input[i] + ": ";
            ret += (input.length - i) + "\n";
        }

        return ret;
    }
}