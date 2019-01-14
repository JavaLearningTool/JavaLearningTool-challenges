/**
 * This class represents a type of TestResult that compares expected and actual
 * results and also has some other message.
 */
public class ComparativeMessageTestResult extends MessageTestResult {

    private String actual;
    private String expected;

    /**
     * Constructor for ComparativeTestResultMessage when stdout is null
     * 
     * @param expected the expected output
     * @param actual   the actual output
     * @param label    some information about the TestResult (used as a title)
     * @param message  some message for the Student
     * @param passed   whether or not the student passed the test case
     * @param time     How long the test case took
     */
    public ComparativeMessageTestResult(String expected, String actual, String label, String message, boolean passed,
            long time) {
        this(expected, actual, label, message, passed, time, null);
    }

    /**
     * Constructor for ComparativeTestResultMessage when stdout is not null
     * 
     * @param expected   the expected output
     * @param actual     the actual output
     * @param label      some information about the TestResult (used as a title)
     * @param message    some message for the Student
     * @param passed     whether or not the student passed the test case
     * @param time       How long the test case took
     * @param consoleOut the output from stdout
     */
    public ComparativeMessageTestResult(String expected, String actual, String label, String message, boolean passed,
            long time, String consoleOut) {
        super(label, message, passed, time, consoleOut);

        this.actual = actual;
        this.expected = expected;
    }

    @Override
    protected void getMiddlePart(StringBuilder builder, boolean timeout, boolean runtimeExceptionOccurred) {
        super.getMiddlePart(builder, timeout, runtimeExceptionOccurred);

        if (builder.length() != 0) {
            builder.append(",");
        }

        // Append on to the json for this TestResult the expected field
        builder.append(getPartString("Expected", (expected == null ? "null" : expected.toString()), false));

        // Add on to the json for this TestResult the actual field if it should be added
        if (!timeout && !runtimeExceptionOccurred) {
            builder.append("," + getPartString("Actual", (actual == null ? "null" : actual.toString()), false));
        }
    }
}