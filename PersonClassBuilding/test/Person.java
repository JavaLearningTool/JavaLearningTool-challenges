public class Person {

    private String name;

    public Person() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGreetingMessage() {
        return "Hello, my name is " + name + ".";
    }
}