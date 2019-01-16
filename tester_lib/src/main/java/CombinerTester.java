import java.util.List;
import java.util.ArrayList;

public class CombinerTester extends RunnableTester {
    private List<RunnableTester> testers = new ArrayList<RunnableTester>();

    public void addTesters(RunnableTester... testers) {
        for (RunnableTester tester : testers) {
            tester.setResultHandler(results -> {
                this.results.addAll(results);
            });
            this.testers.add(tester);
        }
    }

    public void runTests(long time) {
        for (RunnableTester tester : testers) {
            tester.runTests(time);
        }

        resultHandler.accept(results);
    }

    @Override
    public boolean didForm() {
        boolean formed = true;
        for (RunnableTester tester : testers) {
            if (!tester.didForm()) {
                formed = false;
                results.addAll(tester.results);
            }
        }

        return formed;
    }
}