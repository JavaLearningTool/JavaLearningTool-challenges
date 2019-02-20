public class FixMePassByValueTest {

    public static void main(String[] args) {

        // Make tester for this Challenge
        FunctionReturnTester<Void> tester = new FunctionReturnTester<Void>(FixMePassByValueTest::approved, "Test",
                "changeEmployeeName", Employee.class, Void.class);
        tester.noArgsConstructor();

        if (!tester.didForm()) {
            tester.printResults();
            return;
        }

        // test cases
        tester.addArgs(() -> new Person("Chad"));
        tester.addArgs(() -> new Person("Stacy"));
        tester.addArgs(() -> new Person("Jim"));

        tester.setMethodInvoker((obj, arg) -> {
            Person person = (Person) arg[0];
            return tester.getMethod().invoke(obj, (Object) person);
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