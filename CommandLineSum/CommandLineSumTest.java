import java.util.List;

public class CommandLineSumTest {

	public static void main(String[] args) {

		// Make tester for this Challenge
		CommandLineStandardOutTester tester =
			new CommandLineStandardOutTester(CommandLineSumTest::approved, "Test");

		if (!tester.didForm()) {
            tester.printResults();
            return;
        }

		// Will accept results with or without spaces and with or without new lines
        MethodTester.useLooseStringEquality(tester);

        // Run test case
        String[][] testArgs = {{"1"}, {"1", "2", "3", "4", "5"}, {"-1", "333", "-9", "7"},
                               {"77", "33"}};
		tester.runTests(testArgs);
	}

	public static void approved(String[] args) {
        
        int sum = 0;

        for (int i = 0; i < args.length; i++) {
            sum += Integer.parseInt(args[i]);
        }

        System.out.println(sum + " ");

	}


}