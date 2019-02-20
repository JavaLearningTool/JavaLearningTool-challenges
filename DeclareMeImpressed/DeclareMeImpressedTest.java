public class DeclareMeImpressedTest {
    public static void main(String[] args) {

        ParseTester tester = new ParseTester("Test.java", args);

        if (!tester.didForm()) {
            // If tester does not form, print out whatever caused it to fail
            tester.printResults();
            return;
        }

        tester.requireVariableDeclaration("main", "int", "i");

        tester.printResults();
    }
}