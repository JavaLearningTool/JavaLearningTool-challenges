
public class HouseClassBuildingTest extends ClassTester {

    public static void main(String[] args) {
        new HouseClassBuildingTest();
    }

    public HouseClassBuildingTest() {
        super("HouseClassBuildingTest$House", "House");

        if (!didForm()) {
            printResults();
            return;
        }

        final String[] addresses = { "3 Georgia Tech Station", "1 Infinity Loop", "252 Peachtree Street" };
        final String[] colors = { "Green", "Yellow", "White" };
        final int[] ages = { 0, 67, 130 };

        addConstructorTest(addresses[0], colors[0], ages[0]);
        addConstructorTest(addresses[1], colors[1], ages[1]);

        addStringGetterSetterTest("address", addresses[0], colors[0], ages[0], addresses[1]);
        addStringGetterSetterTest("address", addresses[0], colors[0], ages[0], addresses[2]);

        addStringGetterSetterTest("color", addresses[0], colors[0], ages[0], colors[1]);
        addStringGetterSetterTest("color", addresses[0], colors[0], ages[0], colors[2]);

        addNumberGetterSetterTest("age", addresses[0], colors[0], ages[0], ages[1]);
        addNumberGetterSetterTest("age", addresses[0], colors[0], ages[0], ages[2]);

        startGroup("Testing addNeighbor, getNeighbor, and getNeighbors.");
        addFormatConstructor("constructor", "House %s = new House(\"%s\", \"%s\", %d);", "h1", addresses[0], colors[0],
                ages[0]);
        addFormatConstructor("constructor", "House %s = new House(\"%s\", \"%s\", %d);", "h2", addresses[1], colors[1],
                ages[1]);
        addFormatConstructor("constructor", "House %s = new House(\"%s\", \"%s\", %d);", "h3", addresses[2], colors[1],
                ages[2]);
        addFormatChunk("addNeighbor", "%s.addNeighbor(%s);", "h1", "h2");
        addFormatChunk("addNeighbor", "%s.addNeighbor(%s);", "h1", "h3");
        addFormatChunk("getNeighbor", "%s.getNeighbor(%d);", "h1", 0);
        addFormatChunk("getNeighbors", "%s.getNeighbors();", "h1");

        startGroup("Testing addNeighbor, getNeighbor, and getNeighbors.");
        addFormatConstructor("constructor", "House %s = new House(\"%s\", \"%s\", %d);", "h1", addresses[0], colors[0],
                ages[0]);
        addFormatConstructor("constructor", "House %s = new House(\"%s\", \"%s\", %d);", "h2", addresses[1], colors[1],
                ages[1]);
        addFormatConstructor("constructor", "House %s = new House(\"%s\", \"%s\", %d);", "h3", addresses[2], colors[2],
                ages[2]);
        addFormatChunk("addNeighbor", "%s.addNeighbor(%s);", "h1", "h2");
        addFormatChunk("addNeighbor", "%s.addNeighbor(%s);", "h1", "h3");
        addFormatChunk("addNeighbor", "%s.addNeighbor(%s);", "h1", "h3");
        addFormatChunk("getNeighbor", "%s.getNeighbor(%d);", "h1", 1);
        addFormatChunk("getNeighbors", "%s.getNeighbors();", "h1");

        startGroup("Testing pickOldestHouse and getLastBuilt.");
        addFormatConstructor("constructor", "House %s = new House(\"%s\", \"%s\", %d);", "h1", addresses[0], colors[0],
                ages[0]);
        addStaticFormatChunk("getLastBuilt", "House h5 = House.getLastBuilt();");
        addFormatConstructor("constructor", "House %s = new House(\"%s\", \"%s\", %d);", "h2", addresses[1], colors[1],
                ages[1]);
        addStaticFormatChunk("getLastBuilt", "House h6 = House.getLastBuilt();");
        addStaticChunk("pickOldestHouse", "House h7 = House.pickOldestHouse(new House[]{h1, h2});",
                (Object) new Object[] { "h1", "h2" });

        startGroup("Testing pickOldestHouse and getLastBuilt.");
        addFormatConstructor("constructor", "House %s = new House(\"%s\", \"%s\", %d);", "h1", addresses[1], colors[1],
                ages[1]);
        addFormatConstructor("constructor", "House %s = new House(\"%s\", \"%s\", %d);", "h2", addresses[0], colors[0],
                ages[0]);
        addStaticFormatChunk("getLastBuilt", "House h5 = House.getLastBuilt();");
        addFormatConstructor("constructor", "House %s = new House(\"%s\", \"%s\", %d);", "h3", addresses[2], colors[2],
                ages[2]);
        addStaticFormatChunk("getLastBuilt", "House h6 = House.getLastBuilt();");
        addStaticChunk("pickOldestHouse", "House h7 = House.pickOldestHouse(new House[]{h1, h2, h3});",
                (Object) new Object[] { "h1", "h2", "h3" });

        runTests(200);

    }

    private void addStringGetterSetterTest(String field, String addr, String col, int age, String newObj) {
        startGroup("Testing " + field + " getters and setters.");
        addFormatConstructor("constructor", "House %s = new House(\"%s\", \"%s\", %d);", "h", addr, col, age);
        String cap = field.substring(0, 1).toUpperCase() + field.substring(1);
        addChunk("set" + cap, "h.set" + cap + "(\"" + newObj + "\");", "h", newObj);
        addChunk("get" + cap, "h.get" + cap + "();", "h");
    }

    private void addNumberGetterSetterTest(String field, String addr, String col, int age, Object newObj) {
        startGroup("Testing " + field + " getters and setters.");
        addFormatConstructor("constructor", "House %s = new House(\"%s\", \"%s\", %d);", "h", addr, col, age);
        String cap = field.substring(0, 1).toUpperCase() + field.substring(1);
        addChunk("set" + cap, "h.set" + cap + "(" + newObj + ");", "h", newObj);
        addChunk("get" + cap, "h.get" + cap + "();", "h");
    }

    private void addConstructorTest(String addr, String col, int age) {
        startGroup("Testing constructor and getters.");
        addFormatConstructor("constructor", "House %s = new House(\"%s\", \"%s\", %d);", "h", addr, col, age);
        addChunk("getAddress", "h.getAddress();", "h");
        addChunk("getColor", "h.getColor();", "h");
        addChunk("getAge", "h.getAge();", "h");
    }

    public static class House {

        @TestedMember(name = "address", equality = EqualityTester.OBJECT)
        private String address;
        @TestedMember(name = "color", equality = EqualityTester.OBJECT)
        private String color;
        @TestedMember(name = "age", equality = EqualityTester.OBJECT)
        private int age;

        // Don't include fields that are the type of the class
        private House[] neighbors = new House[5];

        private int neighborCount = 0;
        private static House lastBuilt;

        @TestedMember(name = "constructor")
        public House(String addr, String col, int age) {
            address = addr;
            color = col;
            this.age = age;
            lastBuilt = this;
        }

        @TestedMember(name = "getAddress", equality = EqualityTester.OBJECT)
        public String getAddress() {
            return address;
        }

        @TestedMember(name = "setAddress")
        public void setAddress(String addr) {
            address = addr;
        }

        @TestedMember(name = "getColor", equality = EqualityTester.OBJECT)
        public String getColor() {
            return color;
        }

        @TestedMember(name = "setColor")
        public void setColor(String col) {
            color = col;
        }

        @TestedMember(name = "getAge", equality = EqualityTester.OBJECT)
        public int getAge() {
            return age;
        }

        @TestedMember(name = "setAge")
        public void setAge(int a) {
            age = a;
        }

        @TestedMember(name = "getNeighbors", returnIsClass = true, equality = EqualityTester.ARRAY)
        public House[] getNeighbors() {
            return neighbors;
        }

        @TestedMember(name = "addNeighbor", paramIsClass = { 0 })
        public void addNeighbor(House ne) {
            neighbors[neighborCount++] = ne;
        }

        @TestedMember(name = "getNeighbor", returnIsClass = true, equality = EqualityTester.OBJECT)
        public House getNeighbor(int n) {
            return neighbors[n];
        }

        @TestedMember(name = "pickOldestHouse", returnIsClass = true, paramIsClass = {
                0 }, equality = EqualityTester.OBJECT)
        public static House pickOldestHouse(House[] houses) {
            House oldest = houses[0];
            for (int i = 1; i < houses.length; i++) {
                if (houses[i].age > oldest.age) {
                    oldest = houses[i];
                }
            }

            return oldest;
        }

        @TestedMember(name = "getLastBuilt", returnIsClass = true, equality = EqualityTester.OBJECT)
        public static House getLastBuilt() {
            return lastBuilt;
        }

        @Override
        public boolean equals(Object other) {
            return ClassTester.tester.fieldsEqual(this, other);
        }

    }
}