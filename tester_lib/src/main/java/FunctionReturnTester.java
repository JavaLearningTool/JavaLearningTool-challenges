import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
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
import java.util.Map;
import java.util.HashMap;

/**
 * Tests Methods by supplying input and looking at the output
 * 
 * Also tests side effects on parameters
 * 
 * @param O the return type of the method
 */
public class FunctionReturnTester<O> extends MethodTester<O> {

    private static final int MODIFIERS = Modifier.PUBLIC;
    protected Function<Object[], O> expectedFunction;

    protected ActualMethodInvoker<Object> methodInvoker;
    protected List<Supplier<Object>[]> args = new ArrayList<Supplier<Object>[]>();
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

    /**
     * Adds a new test case with the given inputs
     * 
     * @param args var args Supplier objects that return the nth input to the method
     * for this test case
     */
    public void addArgs(Supplier<Object>... args) {
        this.args.add(args);
    }

    /**
     * Adds a new test case with the given number inputs. Use this if all inputs are primitive numbers
     * 
     * @param args var args primitive Numbers where the nth item is the nth input to the method
     * for this test case
     */
    public void addArgs(Number... args) {
        Supplier<Object>[] suppliers = new Supplier[args.length];
        
        for (int i = 0; i < suppliers.length; i++) {
            Object arg = args[i];
            suppliers[i] = () -> arg;
        }

        addArgs(suppliers);
    }

    /**
     * Adds a new test case with the given boolean inputs. Use this if all inputs are booleans
     * 
     * @param args var args booleans where the nth item is the nth input to the method
     * for this test case
     */
    public void addArgs(boolean... args) {
        Supplier<Object>[] suppliers = new Supplier[args.length];
        
        for (int i = 0; i < suppliers.length; i++) {
            Object arg = args[i];
            suppliers[i] = () -> arg;
        }

        addArgs(suppliers);
    }

    /**
     * Adds a new test case with the given char inputs. Use this if all inputs are chars
     * 
     * @param args var args chars where the nth item is the nth input to the method
     * for this test case
     */
    public void addArgs(char... args) {
        Supplier<Object>[] suppliers = new Supplier[args.length];
        
        for (int i = 0; i < suppliers.length; i++) {
            Object arg = args[i];
            suppliers[i] = () -> arg;
        }

        addArgs(suppliers);
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

            // Get inputs
            Supplier[] argsSupplier = args.get(i);
            Object[] expectedIn = new Object[argsSupplier.length];
            Object[] actualIn = new Object[argsSupplier.length];
            for (int j = 0; j < expectedIn.length; j++) {
                expectedIn[j] = argsSupplier[j].get();
                actualIn[j] = argsSupplier[j].get();
            }

            try {
                out = expectedFunction.apply(expectedIn);
            } catch (Throwable t) {
                ex = t;
            }
            final Throwable expectedException = ex;
            final O expectedOut = out;

            // Must be final to be accessed from inner class
            final String input = inToString.apply(expectedIn);
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

                        actualOut = (O) methodInvoker.apply(actualObject, actualIn);

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

                    TestResult result = null;
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
                        if (expectedOut == null || actualOut == null) { // Null check
                            passed = actualOut == expectedOut; // Pass only if both are null
                        } else if (equalityTester != null) { // Use equality tester if we have it
                            passed = equalityTester.test(expectedOut, actualOut);
                        } else { // Use smart equals
                            passed = TestUtils.smartEquals(expectedOut, actualOut);
                        }

                        String expectedString = expectedOut == null ? "null" : outToString.apply(expectedOut);
                        String actualString = actualOut == null ? "null" : outToString.apply(actualOut);

                        if (!passed) { // If actual is not equal to expected
                            result = new ComparativeTestResult(input, expectedString,
                                actualString, false, System.currentTimeMillis() - startTime, false,
                                consoleOut);
                        } else {
                            // Check possible side effects
                            for (int j = 0; j < expectedIn.length; j++) {
                                Object expectedSideEffect = expectedIn[j];
                                Object actualSideEffect = actualIn[j];

                                // If side effect wasn't same in actual and expected
                                if (!TestUtils.smartEquals(expectedSideEffect, actualSideEffect)) {
                                    passed = false;
                                    //String expected, String actual, String label, String message, boolean passed, long time, String consoleOut
                                    result = new ComparativeMessageTestResult(TestUtils.smartToString(expectedSideEffect), TestUtils.smartToString(actualSideEffect),
                                        "Parameter " + j + " modified in unexpected way.", null, false, System.currentTimeMillis() - startTime,
                                        consoleOut);
                                    break;
                                }
                            }

                            // All side effects were properly applied
                            if (passed) {
                                result = new ComparativeTestResult(input, expectedString,
                                    actualString, true, System.currentTimeMillis() - startTime, false,
                                    consoleOut);
                            }
                        }
                        
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