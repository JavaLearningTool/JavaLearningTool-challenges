import java.util.List;

public class HelloWorldTest {

	public static void main(String[] args) {

		// Make tester for this Challenge
		CommandLineStandardOutTester tester = new CommandLineStandardOutTester(HelloWorldTest::approved, "Test");

		if (!tester.didForm()) {
			tester.printResults();
			return;
		}

		// Will accept results with or without spaces and with or without new lines
		MethodTester.useLooseStringEquality(tester);

		// Run test case
		String[][] testArgs = { {} };
		tester.runTests(testArgs);
	}

	public static void approved(String[] args) {
		System.out.println("Hello World");
	}

}