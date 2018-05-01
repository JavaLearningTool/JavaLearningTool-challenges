public class ComparativeMessageTestResult extends MessageTestResult {

    private String actual;
    private String expected;

    public ComparativeMessageTestResult(String expected, String actual, String info, String message, boolean passed,
            long time) {
        this(expected, actual, info, message, passed, time, null);
    }

    public ComparativeMessageTestResult(String expected, String actual, String info, String message, boolean passed,
            long time, String consoleOut) {
        super(info, message, passed, time, consoleOut);

        this.actual = actual;
        this.expected = expected;
    }

    @Override
    protected void getMiddlePart(StringBuilder builder, boolean timeout, boolean runtimeExceptionOccurred) {
        super.getMiddlePart(builder, timeout, runtimeExceptionOccurred);

        if (builder.length() != 0) {
            builder.append(",");
        }

        builder.append(getPartString("Expected", (expected == null ? "null" : expected.toString()), false));

        if (!timeout && !runtimeExceptionOccurred) {
            builder.append("," + getPartString("Actual", (actual == null ? "null" : actual.toString()), false));
        }
    }
}