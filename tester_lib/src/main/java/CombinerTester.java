import java.util.List;
import java.util.ArrayList;

public class CombinerTester extends Tester {
    List<Tester> testers = new ArrayList<Tester>();

    public void addTesters(Tester... testers) {
        for (Tester tester : testers) {
            tester.setResultHandler(results -> {
                this.results.addAll(results);
            });
            this.testers.add(tester);
        }
    }

    public void runTests(long time) {
        for (Tester tester : testers) {
            tester.runTests(time);
        }

        resultHandler.accept(results);
    }

    @Override
    public boolean didForm() {
        boolean formed = true;
        for (Tester tester : testers) {
            if (!tester.didForm()) {
                formed = false;
                results.addAll(tester.results);
            }
        }

        return formed;
    }
}