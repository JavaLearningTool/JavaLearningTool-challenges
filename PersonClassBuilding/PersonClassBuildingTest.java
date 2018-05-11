public class PersonClassBuildingTest extends ClassTester {

    public static void main(String[] args) {
        // Create an instance of the tester
        new PersonClassBuildingTest();
    }

    public PersonClassBuildingTest() {
        // Call super constructor with Expected class name, Actual class name
        super("PersonClassBuildingTest$Person", "Person");

        // Check to see if tester formed
        if (!didForm()) {
            printResults();
            return;
        }

        // Create group named getters and setters for test case 1
        startGroup("Testing getters and setters.");
        addConstructor("constructor", "Person p = new Person();", "p");
        addChunk("setName", "p.setName(\"Chad\");", "p", "Chad");
        addChunk("getName", "p.getName();", "p");

        // Create group named getters and setters for test case 2
        startGroup("Testing getters and setters.");
        addConstructor("constructor", "Person p = new Person();", "p");
        addChunk("setName", "p.setName(\"Dan\");", "p", "Dan");
        addChunk("getName", "p.getName();", "p");

        // Create group named getGreetingMessage for test case 3
        startGroup("Testing getGreetingMessage method.");
        addConstructor("constructor", "Person p = new Person();", "p");
        addChunk("setName", "p.setName(\"Chad\");", "p", "Chad");
        addChunk("getGreetingMessage", "p.getGreetingMessage();", "p");

        // Create group named getGreetingMessage for test case 4
        startGroup("Testing getGreetingMessage method.");
        addConstructor("constructor", "Person p = new Person();", "p");
        addChunk("setName", "p.setName(\"Dan\");", "p", "Dan");
        addChunk("getGreetingMessage", "p.getGreetingMessage();", "p");

        // Run all groups
        runTests(200);
    }

    public static class Person {

        private String name;

        @TestedMember()
        public Person() {

        }

        @TestedMember(equality = EqualityTester.OBJECT)
        public String getName() {
            return name;
        }

        @TestedMember()
        public void setName(String name) {
            this.name = name;
        }

        @TestedMember(equality = EqualityTester.OBJECT)
        public String getGreetingMessage() {
            return "Hello, my name is " + name + ".";
        }
    }
}