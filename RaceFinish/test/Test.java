public class Test {
    public String raceFinish(String[] input) {
        String ret = "";

        for (int i = 0; i < input.length; i++) {
            ret += input[i] + ": ";
            ret += (input.length - i) + "\n";
        }

        return ret;
    }
}