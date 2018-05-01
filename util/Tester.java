
import java.util.function.Consumer;

import java.util.ArrayList;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class Tester {

    protected Consumer<List<TestResult>> resultHandler;
    protected ArrayList<TestResult> results = new ArrayList<TestResult>();

    protected boolean failedToForm;

    protected ByteArrayOutputStream baos;
    protected PrintStream ps;
    protected PrintStream oldOut;

    public Tester() {
        captureStandardOut();
        resultHandler = (results) -> {
            printResults();
        };
    }

    private void captureStandardOut() {
        baos = new ByteArrayOutputStream();
        ps = new PrintStream(baos);

        // Only grab oldOut from System.out once
        if (oldOut == null) {
            oldOut = System.out;
        }

        System.setOut(ps);
    }

    protected void resetStandardOut() {
        // Ensure that we are capturing stdout
        System.setOut(ps);

        // Flush out stream so that all output from stdout is ready
        ps.flush();

        // Clears the output stream
        baos.reset();
    }

    protected String getStandarOut() {
        // Flush out stream so that all output from stdout is ready
        ps.flush();
        // Get output from stdout
        String out = baos.toString();
        // Clears the output stream
        baos.reset();
        return out;
    }

    protected void clearStandardOut() {
        baos.reset();
    }

    public boolean didForm() {
        return !failedToForm;
    }

    public ArrayList<TestResult> getResults() {
        return results;
    }

    public void setSingleMessageResult(String label, String message, boolean passed) {
        results = new ArrayList();
        results.add(new MessageTestResult(label, message, passed, 0, false, null));
    }

    public String toJsonString() {
        String ret = "[";
        for (int i = 0; i < results.size(); i++) {
            ret += results.get(i).toJsonString();
            if (i != results.size() - 1) {
                ret += ",";
            }
        }

        ret += "]";
        return ret;
    }

    public void setResultHandler(Consumer<List<TestResult>> resultHandler) {
        this.resultHandler = resultHandler;
    }

    public void printResults() {
        oldOut.println(toJsonString());
    }
}