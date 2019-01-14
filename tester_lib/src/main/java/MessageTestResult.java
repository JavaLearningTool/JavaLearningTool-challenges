public class MessageTestResult extends ConsoleOutTestResult {

    private String message;

    // Timeout
    public MessageTestResult(String info, String message, long time, boolean timeout) {
        this(info, message, time, timeout, null);
    }

    // Timeout
    public MessageTestResult(String info, String message, long time, boolean timeout, String consoleOut) {
        this(info, message, false, time, timeout, null, consoleOut);
    }

    // Exception
    public MessageTestResult(String message, long time, String errorMessage) {
        this(message, time, errorMessage, null);
    }

    public MessageTestResult(String message, long time, String errorMessage, String consoleOut) {
        this("Runtime Exception", message, false, time, false, errorMessage, consoleOut);
    }

    // Normal
    public MessageTestResult(String info, String message, boolean passed, long time) {
        this(info, message, passed, time, null);
    }

    public MessageTestResult(String info, String message, boolean passed, long time, String consoleOut) {
        this(info, message, passed, time, false, null, consoleOut);
    }

    public MessageTestResult(String info, String message, boolean passed, long time, boolean timeout,
            String errorMessage) {
        this(info, message, passed, time, timeout, errorMessage, null);
    }

    public MessageTestResult(String info, String message, boolean passed, long time, boolean timeout,
            String errorMessage, String consoleOut) {
        super(info, passed, time, timeout, errorMessage, consoleOut);

        this.message = message;
    }

    @Override
    protected void getMiddlePart(StringBuilder builder, boolean timeout, boolean runtimeExceptionOccurred) {
        if (builder.length() != 0) {
            builder.append(",");
        }

        builder.append(getPartString("Test", message, true));
    }

}