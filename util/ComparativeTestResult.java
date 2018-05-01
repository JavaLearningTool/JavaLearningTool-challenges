public class ComparativeTestResult extends ConsoleOutTestResult {
    private String expected;
    private String actual;

    // Timeout
    public ComparativeTestResult(String input, String expected, boolean timeout, long time) {
        this(input, expected, timeout, time, null);
    }

    // Timeout
    public ComparativeTestResult(String input, String expected, boolean timeout, long time, String consoleOut) {
        this(input, expected, null, false, time, timeout, null, consoleOut);
    }

    // Runtime Exception
    public ComparativeTestResult(String input, String expected, String errorMessage, long time) {
        this(input, expected, errorMessage, time, null);
    }

    public ComparativeTestResult(String input, String expected, String errorMessage, long time, String consoleOut) {
        this(input, expected, null, false, time, false, errorMessage, consoleOut);
    }

    // Normal
    public ComparativeTestResult(String input, String expected, String actual, boolean passed, long time,
            boolean timeout) {
        this(input, expected, actual, passed, time, timeout, null);
    }

    public ComparativeTestResult(String input, String expected, String actual, boolean passed, long time,
            boolean timeout, String consoleOut) {
        this(input, expected, actual, passed, time, timeout, null, consoleOut);
    }

    public ComparativeTestResult(String input, String expected, String actual, boolean passed, long time,
            boolean timeout, String errorMessage, String consoleOut) {

        super("Input: " + ((input == null || input == "") ? "None" : input), passed, time, timeout, errorMessage,
                consoleOut);

        this.expected = expected;
        this.actual = actual;
    }

    @Override
    protected void getMiddlePart(StringBuilder builder, boolean timeout, boolean runtimeExceptionOccurred) {
        if (builder.length() != 0) {
            builder.append(",");
        }

        builder.append(getPartString("Expected", (expected == null ? "null" : expected.toString()), false));

        if (!timeout && !runtimeExceptionOccurred) {
            builder.append("," + getPartString("Actual", (actual == null ? "null" : actual.toString()), false));
        }
    }
}