import java.util.function.BiFunction;
import java.util.function.Function;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import java.io.StringWriter;
import java.io.PrintWriter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class FunctionReturnTester<O> extends MethodTester<O> {

    private static final int MODIFIERS = Modifier.PUBLIC;
    protected Function<Object[], O> expectedFunction;

    protected ActualMethodInvoker<Object> methodInvoker;
    protected List<Object[]> args = new ArrayList<Object[]>();
    protected Function<Object[], String> inToString;
    protected Function<O, String> outToString;

    public FunctionReturnTester(Function<Object[], O> expectedFunction, String className, String methodName,
            Class<?> returnType, Class<?>... paramTypes) {
        super(className, methodName, MODIFIERS, returnType, paramTypes);
        this.expectedFunction = expectedFunction;
    }

    public void setMethodInvoker(ActualMethodInvoker<Object> methodInvoker) {
        this.methodInvoker = methodInvoker;
    }

    public void addArgs(Object... args) {
        this.args.add(args);
    }

    public void setInputToStringConverter(Function<Object[], String> inToString) {
        this.inToString = inToString;
    }

    public void setOutputToStringConverter(Function<O, String> outToString) {
        this.outToString = outToString;
    }

    public void runTests() {
        runTests(200);
    }

    public void runTests(long limit) {

        if (failedToForm) {
            throw new RuntimeException("Can't use a tester that didn't fully form.");
        }

        final AtomicInteger numTest = new AtomicInteger();

        for (int i = 0; i < args.size(); i++) {

            numTest.set(i);
            results.add(null);
            long startTime = System.currentTimeMillis();

            // Call expected function capture return value or Throwable
            Throwable ex = null;
            O out = null;
            try {
                out = expectedFunction.apply(args.get(i));
            } catch (Throwable t) {
                ex = t;
            }
            final Throwable expectedException = ex;
            final O expectedOut = out;

            // Must be final to be accessed from inner class
            final String input = inToString.apply(args.get(i));
            AtomicBoolean testFinished = new AtomicBoolean(false);

            Runnable testRunner = new Runnable() {
                public void run() {
                    Exception exception = null;
                    O actualOut = null;

                    resetStandardOut();

                    try {
                        // Call the method that gives actual results
                        Object actualObject = null;
                        if (!staticMethod) {
                            actualObject = constructor.newInstance();
                        }

                        Object[] testArgs = args.get(numTest.get());

                        actualOut = (O) methodInvoker.apply(actualObject, testArgs);

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
                    String consoleOut = getStandardOut();

                    if (expectedException != null) { // We expect the actual method to throw something

                        String expectedExceptionDescription = getExceptionMessageString(expectedException);
                        String actualExceptionDescription = getExceptionMessageString(exception);

                        boolean passed = true;

                        // if actual exception is null or the exceptions classes aren't equal or
                        // (expected exception's message isn't an empty String and the messages aren't
                        // equal) then you didn't pass
                        if (exception == null || !expectedException.getClass().equals(exception.getClass())
                                || (!expectedException.getMessage().equals("")
                                        && !expectedException.getMessage().equals(exception.getMessage()))) {

                            passed = false;
                        }

                        result = new ComparativeTestResult(input, expectedExceptionDescription,
                                actualExceptionDescription, passed, System.currentTimeMillis() - startTime, false,
                                consoleOut);

                    } else if (exception != null) { // We didn't expect the actual method to throw something but it did

                        // Get the exceptions Stack Trace as a String
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        exception.printStackTrace(pw);
                        String stackTrace = sw.toString();

                        result = new ComparativeTestResult(input, outToString.apply(expectedOut), stackTrace,
                                System.currentTimeMillis() - startTime, consoleOut);
                    } else { // Nothing thrown
                        // Use equalityTester if available to test if actual equals expected
                        // otherwise use .equals()
                        boolean passed = false;
                        if (equalityTester != null) {
                            passed = equalityTester.test(expectedOut, actualOut);
                        } else {
                            passed = expectedOut.equals(actualOut);
                        }

                        result = new ComparativeTestResult(input, outToString.apply(expectedOut),
                                outToString.apply(actualOut), passed, System.currentTimeMillis() - startTime, false,
                                consoleOut);
                    }

                    results.set(numTest.get(), result);
                    testFinished.set(true);
                }
            };

            Thread t = new Thread(testRunner);
            t.start();

            try {
                t.join(limit);
                t.stop();
                if (!testFinished.get()) {
                    // Timeout
                    String consoleOut = getStandardOut();
                    clearStandardOut();
                    ComparativeTestResult result = new ComparativeTestResult(input, outToString.apply(expectedOut),
                            true, System.currentTimeMillis() - startTime, consoleOut);
                    results.set(numTest.get(), result);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        resultHandler.accept(results);
    }

    /**
     * Return a message based on the Throwable for use in TestResults
     * 
     * @param exception the Throwable to use when making the message
     * @return a String to use in TestResults
     */
    private String getExceptionMessageString(Throwable exception) {
        // Exception is null
        if (exception == null) {
            return "Throws nothing.";
        }

        // Exception has a message that isn't just an empty String
        if (!exception.getMessage().equals("")) {
            return String.format("Throws %s with message: \"%s\".", exception.getClass().getSimpleName(),
                    exception.getMessage());
        }

        // Exception message just an empty String
        return String.format("Throws %s.", exception.getClass().getSimpleName());
    }

}