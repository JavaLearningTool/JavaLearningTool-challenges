/**
 * This class represents a type of TestResult that includes some standard out
 * form the Student's code
 */
public abstract class ConsoleOutTestResult extends TestResult {

    private String consoleOut;

    // limit how much standard out to include to make for smaller json
    private static final int MAX_CONSOLE_SIZE = 500;

    /**
     * Constructor for ConsoleOutTestResult
     * 
     * @param label        something to describe the test case
     * @param passed       whether or not the student's code passed
     * @param time         how long the test took
     * @param timeout      whether or not the student's code timed out
     * @param errorMessage the error message from the student's code if an Exception
     *                     occurred
     * @param consoleOut   Student's output to stdout
     */
    public ConsoleOutTestResult(String label, boolean passed, long time, boolean timeout, String errorMessage,
            String consoleOut) {
        super(label, passed, time, timeout, errorMessage);

        if (consoleOut != null) {
            // If console log is too large
            if (consoleOut.length() > MAX_CONSOLE_SIZE) {
                consoleOut = consoleOut.substring(0, MAX_CONSOLE_SIZE);
                consoleOut += "...";
            }
            this.consoleOut = consoleOut;
        }
    }

    @Override
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

    /**
     * This method adds some json attributes to the middle of the json String
     * 
     * @param builder                  the StringBuilder that will be turned into
     *                                 the json String
     * @param timeout                  whether or not the test case timed out
     * @param runtimeExceptionOccurred whether or not a RuntimeException occurred
     */
    protected abstract void getMiddlePart(StringBuilder builder, boolean timeout, boolean runtimeExceptionOccurred);

}