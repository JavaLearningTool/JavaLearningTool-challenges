public class Test {
    public double findAverage(double[] input) {
        double sum = 0;

        for (double d : input) {
            sum += d;
        }

        return sum / input.length;
    }
}