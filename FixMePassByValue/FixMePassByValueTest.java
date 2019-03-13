public class FixMePassByValueTest {

    public static void main(String[] args) {

        // Make tester for this Challenge
        FunctionReturnTester<Void> tester = new FunctionReturnTester<Void>(FixMePassByValueTest::approved, "Test",
                "changeEmployeeName", void.class, Employee.class, String.class);
        tester.noArgsConstructor();

        if (!tester.didForm()) {
            tester.printResults();
            return;
        }

        // test cases
        tester.addArgs(() -> new Employee("Chad"), () -> "Chaderick");
        tester.addArgs(() -> new Employee("Stacy"), () -> "Tod");
        tester.addArgs(() -> new Employee("Jim"), () -> "Jimbo");

        tester.setMethodInvoker((obj, arg) -> {
            Employee emp = (Employee) arg[0];
            String newName = (String) arg[1];
            return tester.getMethod().invoke(obj, (Object) emp, (Object) newName);
        });

        tester.setInputToStringConverter((arg) -> {
            Employee emp = (Employee) arg[0];
            String newName = (String) arg[1];

            return String.format("employee = %s, newName = \"%s\"", emp, newName);
        });

        // Method is void
        tester.setOutputToStringConverter(arg -> "No return");

        // Run test cases
        tester.runTests();
    }

    public static Void approved(Object[] params) {
        Employee emp = (Employee) params[0];
        String newName = (String) params[1];
        emp.setName(newName);

        return null;
    }
}