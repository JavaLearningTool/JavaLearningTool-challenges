import java.util.List;

public class BirthdayMessageTest {

    public static void main(String[] args) {

        // Make tester for this Challenge
        CommandLineStandardOutTester tester = new CommandLineStandardOutTester(BirthdayMessageTest::approved, "Test");

        if (!tester.didForm()) {
            tester.printResults();
            return;
        }

        // Will accept results with or without spaces and with or without new lines
        MethodTester.useLooseStringEquality(tester);

        // Run test case
        String[][] testArgs = { { "32" }, { "0" }, { "-4" }, { "1" }, { "4" }, { "5" }, { "11" }, { "12" }, { "19" },
                { "20" }, { "21" }, { "22" } };
        tester.runTests(testArgs);
    }

    public static void approved(String[] args) {
        int input = Integer.parseInt(args[0]);

        if (input < 0) {
            System.out.println("That's not a real age!");
            return;
        }

        if (input <= 4) {
            System.out.println("You're a baby.");
        }

        if (input <= 12) {
            System.out.println("You're so very young.");
        } else if (input < 20) {
            System.out.println("You must be a teenager...");
        } else if (input == 20) {
            System.out.println("Congrats. You made it.");
        }

        if (input < 21) {
            System.out.println("Sorry, age to drink in America is 21.");
        } else {
            System.out.println("You're an adult!");
        }
    }

}