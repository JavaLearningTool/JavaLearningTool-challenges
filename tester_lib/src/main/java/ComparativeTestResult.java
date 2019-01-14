/**
 * This class represents a type of TestResult that compares expected and actual
 * results
 */
public class ComparativeTestResult extends ConsoleOutTestResult {
    private String expected;
    private String actual;

    /**
     * This constructor is used if a timeout occurred and there is no stdout
     * 
     * @param label    the input for the test case
     * @param expected the expected output
     * @param timeout  whether or not a timeout occurred
     * @param time     how long the test case took
     */
    public ComparativeTestResult(String label, String expected, boolean timeout, long time) {
        this(label, expected, timeout, time, null);
    }

    /**
     * This constructor is used if a timeout occurred
     * 
     * @param label      the input for the test case
     * @param expected   the expected output
     * @param timeout    whether or not a timeout occurred
     * @param time       how long the test case took
     * @param consoleOut the student's stdout from the test case
     */
    public ComparativeTestResult(String label, String expected, boolean timeout, long time, String consoleOut) {
        this(label, expected, null, false, time, timeout, null, consoleOut);
    }

    /**
     * This constructor is used if a RuntimeException occurred and there is no
     * stdout
     * 
     * @param label        the input for the test case
     * @param expected     the expected output
     * @param errorMessage the error message for the RuntimeException that occurred
     * @param time         how long the test case took
     */
    public ComparativeTestResult(String label, String expected, String errorMessage, long time) {
        this(label, expected, errorMessage, time, null);
    }

    /**
     * This constructor is used if a RuntimeException occurred
     * 
     * @param label        the input for the test case
     * @param expected     the expected output
     * @param errorMessage the error message for the RuntimeException that occurred
     * @param time         how long the test case took
     * @param consoleOut   the student's stdout from the test case
     */
    public ComparativeTestResult(String label, String expected, String errorMessage, long time, String consoleOut) {
        this(label, expected, null, false, time, false, errorMessage, consoleOut);
    }

    /**
     * This constructor is used if a test case finished normally and there is no
     * stdout
     * 
     * @param label    the input for the test case
     * @param expected the expected output
     * @param actual   the actual output
     * @param passed   whether or not the Student passed the test case
     * @param time     how long the test case took
     */
    public ComparativeTestResult(String label, String expected, String actual, boolean passed, long time,
            boolean timeout) {
        this(label, expected, actual, passed, time, timeout, null);
    }

    /**
     * This constructor is used if a test case finished normally
     * 
     * @param label      the input for the test case
     * @param expected   the expected output
     * @param actual     the actual output
     * @param passed     whether or not the Student passed the test case
     * @param time       how long the test case took
     * @param consoleOut the student's stdout from the test case
     */
    public ComparativeTestResult(String label, String expected, String actual, boolean passed, long time,
            boolean timeout, String consoleOut) {
        this(label, expected, actual, passed, time, timeout, null, consoleOut);
    }

    /**
     * This constructor is used if a test case finished normally
     * 
     * @param label        the input for the test case
     * @param expected     the expected output
     * @param actual       the actual output
     * @param passed       whether or not the Student passed the test case
     * @param time         how long the test case took
     * @param timeout      whether or not a timeout occurred
     * @param errorMessage the error message for the RuntimeException that occurred
     * @param consoleOut   the student's stdout from the test case
     */
    public ComparativeTestResult(String label, String expected, String actual, boolean passed, long time,
            boolean timeout, String errorMessage, String consoleOut) {

        super("Input: " + ((label == null || label == "") ? "None" : label), passed, time, timeout, errorMessage,
                consoleOut);

        this.expected = expected;
        this.actual = actual;
    }

    @Override
    protected void getMiddlePart(StringBuilder builder, boolean timeout, boolean runtimeExceptionOccurred) {
        if (builder.length() != 0) {
            builder.append(",");
        }

        // Append expected into the json
        builder.append(getPartString("Expected", (expected == null ? "null" : expected.toString()), false));

        // Append actual into the json if it should be added
        if (!timeout && !runtimeExceptionOccurred) {
            builder.append("," + getPartString("Actual", (actual == null ? "null" : actual.toString()), false));
        }
    }
}