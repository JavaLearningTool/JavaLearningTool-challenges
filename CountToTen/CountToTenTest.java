import java.util.List;

public class CountToTenTest {

	public static void main(String[] args) {

		// Make tester for this Challenge
		CommandLineStandardOutTester tester = new CommandLineStandardOutTester(CountToTenTest::approved, "Test");

		if (!tester.didForm()) {
			tester.printResults();
			return;
		}

		// Will accept results with or without spaces and with or without new lines
		MethodTester.useLooseStringEquality(tester);

		// Run test case
		String[][] testArgs = { { "1" }, { "5" }, { "10" } };
		tester.runTests(testArgs);
	}

	public static void approved(String[] args) {
		int j = Integer.parseInt(args[0]);

		for (int i = 1; i < j; i++) {
			System.out.print(i + ", ");
		}

		System.out.println(j);
	}
}