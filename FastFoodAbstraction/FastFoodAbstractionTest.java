public class FastFoodAbstractionTest {
    public static void main(String[] args) {

        CombinerTester tester = new CombinerTester();

        ChickFilAClassTester chickTester = new ChickFilAClassTester();
        PaneraClassTester paneraTester = new PaneraClassTester();
        FastFoodClassTester fastFoodTester = new FastFoodClassTester();

        tester.addTesters(fastFoodTester, chickTester, paneraTester);

        if (!tester.didForm()) {
            tester.printResults();
            return;
        }

        FunctionReturnTester<Double> eatAtTester = new FunctionReturnTester<Double>(FastFoodAbstractionTest::approved,
                "Test", "eatAt", double.class, FastFood.class, double.class, int.class);

        eatAtTester.noArgsConstructor();

        if (!eatAtTester.didForm()) {
            eatAtTester.printResults();
            return;
        }

        // Use Integer's equals to test return values from expected and actual method
        MethodTester.useLooseDoubleEquality(eatAtTester, .02);

        // Provide eatAtTester with a way to turn arguments into a String
        eatAtTester.setInputToStringConverter((arg) -> {
            double baseCost = (Double) arg[0]; // baseCost
            boolean isChick = (Boolean) arg[1]; // whether to make a chick fil a or Panera
            double arg2 = (Double) arg[2];
            int arg3 = (Integer) arg[3];

            String arg1String = String.format("%s: {baseCost: %.2f}", isChick ? "ChickFilA" : "Panera", baseCost);

            return "restaurant: " + arg1String + ", moneyInMyWallet: " + arg2 + ", numPeople: " + arg3;
        });

        // Convert return value to a String
        eatAtTester.setOutputToStringConverter(out -> out + "");

        // Show the eatAtTester how to parse args and call the actual version of the
        eatAtTester.setMethodInvoker((obj, arg) -> {
            double baseCost = (Double) arg[0]; // baseCost
            boolean isChick = (Boolean) arg[1]; // whether to make a chick fil a or Panera
            FastFood arg1 = null;
            if (isChick) {
                arg1 = (FastFood) chickTester.makeInstance("constructor", baseCost)[1];
            } else {
                arg1 = (FastFood) paneraTester.makeInstance("constructor", baseCost)[1];
            }

            double arg2 = (Double) arg[2];
            int arg3 = (Integer) arg[3];

            // eatAtTester.getMethod() will return the actual method
            return eatAtTester.getMethod().invoke(obj, arg1, arg2, arg3);
        });

        eatAtTester.addArgs(() -> 4.0, () -> true, () -> 23.5, () -> 2 );
        eatAtTester.addArgs(() -> 6.0, () -> false, () -> 45.5, () -> 3 );
        eatAtTester.addArgs(() -> 4.8, () -> true, () -> 100.5, () -> (Integer) 1 ); // 1 is for some reason autoboxed to a boolean
        eatAtTester.addArgs(() -> 2.0, () -> false, () -> 132799.5, () -> 7 );

        tester.addTesters(eatAtTester);

        if (!tester.didForm()) {
            tester.printResults();
            return;
        }

        tester.runTests(200);
    }

    public static double approved(Object[] args) {
        double baseCost = (Double) args[0]; // baseCost
        boolean isChick = (Boolean) args[1]; // whether to make a chick fil a or Panera
        FastFoodClassTester.FastFood arg1 = null;
        if (isChick) {
            arg1 = new ChickFilAClassTester.ChickFilA(baseCost);
        } else {
            arg1 = new PaneraClassTester.Panera(baseCost);
        }

        double arg2 = (Double) args[2];
        int arg3 = (Integer) args[3];

        return arg2 - arg1.costOfFood(arg3);
    }
}