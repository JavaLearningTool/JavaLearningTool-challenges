public class FizzBuzzTest {
    public static void main(String[] args) {
        CommandLineStandardOutTester tester = new CommandLineStandardOutTester(FizzBuzzTest::approved, "Test");

        if (!tester.didForm()) {
            // If tester does not form, print out whatever caused it to fail
            tester.printResults();
            return;
        }

        // Test cases
        tester.addArgs(new String[] { "10" });
        tester.addArgs(new String[] { "12" });
        tester.addArgs(new String[] { "25" });
        tester.addArgs(new String[] { "15" });
        tester.addArgs(new String[] { "6" });
        tester.addArgs(new String[] { "8" });

        // Use loose String equality for new lines but not spaces
        MethodTester.useLooseStringEquality(tester, false, true);

        tester.runTests();
    }

    public static void approved(String[] args) {
        // In this test Students should print out Hello {args[0]}
        int num = Integer.parseInt(args[0]);
        if (num % 3 == 0 && num % 5 == 0) {
            System.out.println("FizzBuzz");
        } else if (num % 3 == 0) {
            System.out.println("Fizz");
        } else if (num % 5 == 0) {
            System.out.println("Buzz");
        }
    }
}