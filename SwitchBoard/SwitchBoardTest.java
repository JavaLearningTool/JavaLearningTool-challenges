public class SwitchBoardTest {
    public static void main(String[] args) {
        
        // Check for switch statement and number of cases
        ParseTester parseTester = new ParseTester("Test.java");

        if (!parseTester.didForm()) {
            // If parseTester does not form, print out whatever caused it to fail
            parseTester.printResults();
            return;
        }

        // 1 switch
        parseTester.requireSwitchStatements(1);
        parseTester.restrictSwitchStatements(1);

        // 4 cases and a default
        parseTester.restrictSwitchEntries(5);

        // 2 breaks
        parseTester.restrictBreakStatements(2);

        // No ifs or ternaries
        parseTester.restrictIfStatements(0);
        parseTester.restrictTernaryExpressions(0);

        // If parsing failed, print out results and return
        if (!parseTester.passed()) {
            parseTester.printResults();
            return;
        }
        
        // Test for propper output
        CommandLineStandardOutTester tester = new CommandLineStandardOutTester(SwitchBoardTest::approved, "Test");

        if (!tester.didForm()) {
            // If tester does not form, print out whatever caused it to fail
            tester.printResults();
            return;
        }

        // Test cases
        tester.addArgs(new String[] { "1-800-725-0000" });
        tester.addArgs(new String[] { "000-000-0000" });
        tester.addArgs(new String[] { "257-123-1234" });
        tester.addArgs(new String[] { "900-000-0000" });
        tester.addArgs(new String[] { "361-123-1234" });
        tester.addArgs(new String[] { "765-199-1234" });
        tester.addArgs(new String[] { "565-109-1244" });

        // Use loose String equality for new lines but not spaces
        MethodTester.useLooseStringEquality(tester, false, true);

        tester.runTests();
    }

    public static void approved(String[] args) {
        int num = Integer.parseInt(args[0].substring(0, 1));

        switch(num) {
            case 0:
                System.out.println("Invalid.");
                break;
            case 1:
                System.out.println("Company calling.");
                break;
            case 2:
            case 3:
                System.out.println("Where is this number from?");
            default:
                System.out.println("Patching you through.");
        }
    }
}