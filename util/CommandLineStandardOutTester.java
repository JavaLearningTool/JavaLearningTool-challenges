import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.io.PrintWriter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import java.util.List;
import java.util.ArrayList;

/**
 * This tester is made for testing via command line args and standard out
 * 
 * Students receive input via the command line args and they print out some
 * output to be tested
 */
public class CommandLineStandardOutTester extends MethodTester<String> {

	// valid modifiers for main method
	private static final int MODIFIERS = Modifier.PUBLIC | Modifier.STATIC;

	// this provides the expected results after consuming the command line args
	protected Consumer<String[]> expectedConsumer;

	// a list of the command line args for all the tests
	protected List<String[]> args = new ArrayList<String[]>();

	/**
	 * Constructor for CommandLineStandardOutTester
	 * 
	 * @param expectedConsumer provides the expected results after consuming command
	 *                         line args
	 * @param className        the name of the Student's class. This class should
	 *                         contain a main method
	 */
	public CommandLineStandardOutTester(Consumer<String[]> expectedConsumer, String className) {
		// Call to super with the inputs that describe testing the main method
		super(className, "main", MODIFIERS, Void.TYPE, String[].class);

		if (!failedToForm) {

			// Test to see if the student's method named main is void
			if (!TestUtils.methodHasReturnType(method, Void.TYPE)) {
				setSingleMessageResult("Method not found.", "Method expected: main(String[]) with return type: void.",
						false);
				failedToForm = true;
			} else {
				this.expectedConsumer = expectedConsumer;
			}

		}
	}

	/**
	 * Add a test case
	 * 
	 * @param args the command line args for this test case
	 */
	public void addArgs(String[] args) {
		this.args.add(args);
	}

	/**
	 * Runs tests for all of the given command line arguments
	 * 
	 * @param args an array of command line arguments (command line arguments are an
	 *             array of Strings)
	 */
	public void runTests(String[][] args) {
		for (String[] arg : args) {
			addArgs(arg);
		}

		runTests();
	}

	/**
	 * Runs all of the test cases given earlier by calling addArgs
	 */
	public void runTests() {

		if (failedToForm) {
			throw new RuntimeException("Can't use a tester that didn't fully form.");
		}

		// Used to keep track of what test we're on
		final AtomicInteger numTest = new AtomicInteger();

		// Loop through all of the test cases
		for (int i = 0; i < args.size(); i++) {

			numTest.set(i);

			// Add a spot for the results of the current test case
			results.add(null);

			// Keep track of when this test case started
			long startTime = System.currentTimeMillis();

			// Convert the input for this test to a String;
			String tempInput = "";
			for (int j = 0; j < args.get(numTest.get()).length; j++) {
				tempInput += args.get(numTest.get())[j];

				if (j < args.get(numTest.get()).length - 1) {
					tempInput += ", ";
				}
			}

			// Clear out stdout just in case there is anything left over
			resetStandardOut();

			// Call the expected version of the test
			expectedConsumer.accept(args.get(numTest.get()));
			final String expectedOut = getStandardOut();
			clearStandardOut();

			// Must be final to be accessed from inner class
			final String input = tempInput;

			// Record whether the test finished or timed out
			AtomicBoolean testFinished = new AtomicBoolean(false);

			// Make a runnable to run this test case on a different thread
			Runnable testRunner = new Runnable() {
				public void run() {
					// Again reset stdout just in case there is something left over
					resetStandardOut();

					// This keeps track of any exceptions that may occur in student code
					Exception exception = null;

					// Keep track of the student output
					String actualOut = null;
					try {
						// Call the method that gives actual results
						method.invoke(null, new Object[] { args.get(numTest.get()) });

						// Get output from stdout
						actualOut = getStandardOut();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						if (e.getCause() instanceof Exception) {
							// If an exception occurred in the student's code
							exception = (Exception) e.getCause();
						} else if (e.getCause() instanceof ThreadDeath) {
							// Do nothing. We timed out
							return;
						} else {
							// If an exception occurred in the tester
							e.printStackTrace();
						}
					} catch (Exception e) {
						// If an exception occurred in the student's code
						exception = e;
					}

					ComparativeTestResult result;

					if (exception != null) {

						// Get the exceptions Stack Trace as a String
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						exception.printStackTrace(pw);
						String stackTrace = sw.toString();

						// Get the students print statements
						String consoleOut = getStandardOut();

						result = new ComparativeTestResult(input, expectedOut, stackTrace,
								System.currentTimeMillis() - startTime, consoleOut);
					} else {

						/*
						 * Test for equality between actual and expected output
						 * 
						 * Use equalityTester if available to test if actual equals expected otherwise
						 * use .equals()
						 */
						boolean passed = false;
						if (equalityTester != null) {
							passed = equalityTester.test(expectedOut, actualOut);
						} else {
							passed = expectedOut.equals(actualOut);
						}

						result = new ComparativeTestResult(input, expectedOut, actualOut, passed,
								System.currentTimeMillis() - startTime, false);
					}

					results.set(numTest.get(), result);
					testFinished.set(true);
				}
			};

			// Create a thread to run the Student code
			Thread t = new Thread(testRunner);
			t.start();

			try {
				// Let the code run for a maximum of 200 milliseconds
				t.join(200);
				/*
				 * Using stop is dangerous but it should be fine as long as we protect shared
				 * resources between Student code and Tester code. Like System.out for example
				 */
				t.stop();

				if (!testFinished.get()) {
					// Timed out
					String consoleOut = getStandardOut();
					ComparativeTestResult result = new ComparativeTestResult(input, expectedOut, true,
							System.currentTimeMillis() - startTime, consoleOut);
					results.set(numTest.get(), result);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// All test cases have run so notify the resultHandler
		resultHandler.accept(results);
	}

}