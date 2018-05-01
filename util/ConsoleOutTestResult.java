public abstract class ConsoleOutTestResult extends TestResult {

    private String consoleOut;
    private static final int MAX_CONSOLE_SIZE = 500;

    public ConsoleOutTestResult(String info, boolean passed, long time, boolean timeout, String errorMessage,
            String consoleOut) {
        super(info, passed, time, timeout, errorMessage);

        if (consoleOut != null) {
            // If console log is too large
            if (consoleOut.length() > MAX_CONSOLE_SIZE) {
                consoleOut = consoleOut.substring(0, MAX_CONSOLE_SIZE);
                consoleOut += "...";
            }
            this.consoleOut = consoleOut;
        }
    }

    protected final StringBuilder getPartsString(boolean timeout, boolean runtimeExceptionOccurred) {
        StringBuilder builder = new StringBuilder("");

        if (runtimeExceptionOccurred) {
            builder.append(getPartString("Console Out", consoleOut + errorMessage, true));
        }

        getMiddlePart(builder, timeout, runtimeExceptionOccurred);

        if (!runtimeExceptionOccurred && consoleOut != null && !consoleOut.equals("")) {
            builder.append("," + getPartString("Console Out", consoleOut, true));
        }

        return builder;
    }

    protected abstract void getMiddlePart(StringBuilder builder, boolean timeout, boolean runtimeExceptionOccurred);

}