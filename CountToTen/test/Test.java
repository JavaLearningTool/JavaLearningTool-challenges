public class Test {
    public static void main(String[] args) {
        int j = Integer.parseInt(args[0]);

		for (int i = 1; i < j; i++) {
			System.out.print(i + ", ");
		}

		System.out.println(j);
    }
}