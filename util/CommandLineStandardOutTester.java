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

public class CommandLineStandardOutTester extends MethodTester<String> {

	private static final int MODIFIERS = Modifier.PUBLIC | Modifier.STATIC;

	protected Consumer<String[]> expectedConsumer;
	protected List<String[]> args = new ArrayList<String[]>();

	public CommandLineStandardOutTester(Consumer<String[]> expectedConsumer, String className) {
		super(className, "main", MODIFIERS, Void.TYPE, String[].class);

		if (!failedToForm) {

			if (!TestUtils.methodHasReturnType(method, Void.TYPE)) {
				setSingleMessageResult("Method not found.", "Method expected: main(String[]) with return type: void.",
						false);
				failedToForm = true;
			} else {
				this.expectedConsumer = expectedConsumer;
			}

		}
	}

	public void addArgs(String[] args) {
		this.args.add(args);
	}

	public void runTests(String[][] args) {
		for (String[] arg : args) {
			addArgs(arg);
		}

		runTests();
	}

	public void runTests() {

		if (failedToForm) {
			throw new RuntimeException("Can't use a tester that didn't fully form.");
		}

		final AtomicInteger numTest = new AtomicInteger();

		for (int i = 0; i < args.size(); i++) {

			numTest.set(i);
			results.add(null);
			long startTime = System.currentTimeMillis();

			// Convert the input for this test to a String;
			String tempInput = "";
			for (int j = 0; j < args.get(numTest.get()).length; j++) {
				tempInput += args.get(numTest.get())[j];

				if (j < args.get(numTest.get()).length - 1) {
					tempInput += ", ";
				}
			}

			resetStandardOut();
			// Call the method to be tested
			expectedConsumer.accept(args.get(numTest.get()));
			final String expectedOut = getStandarOut();
			clearStandardOut();

			// Must be final to be accessed from inner class
			final String input = tempInput;
			AtomicBoolean testFinished = new AtomicBoolean(false);
			Runnable testRunner = new Runnable() {
				public void run() {
					resetStandardOut();

					Exception exception = null;
					String actualOut = null;
					try {
						// Call the method that gives actual results
						method.invoke(null, new Object[] { args.get(numTest.get()) });

						// Get output from stdout
						actualOut = getStandarOut();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						if (e.getCause() instanceof Exception) {
							exception = (Exception) e.getCause();
						} else if (e.getCause() instanceof ThreadDeath) {
							// Do nothing. We timed out
							return;
						} else {
							e.printStackTrace();
						}
					} catch (Exception e) {
						exception = e;
					}

					ComparativeTestResult result;

					if (exception != null) {

						// Get the exceptions Stack Trace as a String
						StringWriter sw = new StringWriter();
						PrintWriter pw = new PrintWriter(sw);
						exception.printStackTrace(pw);
						String stackTrace = sw.toString();

						String consoleOut = getStandarOut();

						result = new ComparativeTestResult(input, expectedOut, stackTrace,
								System.currentTimeMillis() - startTime, consoleOut);
					} else {

						// Use equalityTester if available to test if actual equals expected
						// otherwise use .equals()
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

			Thread t = new Thread(testRunner);
			t.start();

			try {
				t.join(200);
				t.stop();
				if (!testFinished.get()) {
					// Timed out
					String consoleOut = getStandarOut();
					ComparativeTestResult result = new ComparativeTestResult(input, expectedOut, true,
							System.currentTimeMillis() - startTime, consoleOut);
					results.set(numTest.get(), result);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		resultHandler.accept(results);
	}

}