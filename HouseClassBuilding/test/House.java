public class House {

    private String address;
    private String color;
    private int age;

    // Don't include fields that are the type of the class
    private House[] neighbors = new House[5];

    private int neighborCount = 0;
    private static House lastBuilt;

    public House(String addr, String col, int age) {
        address = addr;
        color = col;
        this.age = age;
        lastBuilt = this;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String addr) {
        address = addr;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String col) {
        color = col;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int a) {
        age = a;
    }

    public House[] getNeighbors() {
        return neighbors;
    }

    public void addNeighbor(House ne) {
        neighbors[neighborCount++] = ne;
    }

    public House getNeighbor(int n) {
        return neighbors[n];
    }

    public static House pickOldestHouse(House[] houses) {
        House oldest = houses[0];
        for (int i = 1; i < houses.length; i++) {
            if (houses[i].age > oldest.age) {
                oldest = houses[i];
            }
        }

        return oldest;
    }

    public static House getLastBuilt() {
        return lastBuilt;
    }

}