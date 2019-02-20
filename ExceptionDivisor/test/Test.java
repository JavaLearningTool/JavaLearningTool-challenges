public class Test {
    public double divide(double a, double b) {
        if (b == 0) {
            throw new IllegalArgumentException("Can't divide by 0.");
        }

        return a / b;
    }
}