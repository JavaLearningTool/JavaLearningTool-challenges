public class PersonClassBuildingTest extends ClassTester {

    public static void main(String[] args) {
        new PersonClassBuildingTest();
    }

    public PersonClassBuildingTest() {
        super("PersonClassBuildingTest$Person", "Person");

        if (!didForm()) {
            printResults();
            return;
        }

        startGroup("Testing getters and setters.");
        addConstructor("constructor", "Person p = new Person();", "p");
        addChunk("setName", "p.setName(\"Chad\");", "p", "Chad");
        addChunk("getName", "p.getName();", "p");

        startGroup("Testing getters and setters.");
        addConstructor("constructor", "Person p = new Person();", "p");
        addChunk("setName", "p.setName(\"Dan\");", "p", "Dan");
        addChunk("getName", "p.getName();", "p");

        startGroup("Testing getGreetingMessage method.");
        addConstructor("constructor", "Person p = new Person();", "p");
        addChunk("setName", "p.setName(\"Chad\");", "p", "Chad");
        addChunk("getGreetingMessage", "p.getGreetingMessage();", "p");

        startGroup("Testing getGreetingMessage method.");
        addConstructor("constructor", "Person p = new Person();", "p");
        addChunk("setName", "p.setName(\"Dan\");", "p", "Dan");
        addChunk("getGreetingMessage", "p.getGreetingMessage();", "p");

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