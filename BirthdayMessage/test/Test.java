public class Test {
    public static void main(String[] args) {
        int input = Integer.parseInt(args[0]);
        if (input < 0) {
            System.out.println("That's not a real age!");
            return;
        }

        if (input <= 4) {
            System.out.println("You're a baby.");
        }

        if (input <= 12) {
            System.out.println("You're so very young.");
        } else if (input < 20) {
            System.out.println("You must be a teenager...");
        } else if (input == 20) {
            System.out.println("Congrats. You made it.");
        }

        if (input < 21) {
            System.out.println("Sorry, age to drink in America is 21.");
        } else {
            System.out.println("You're an adult!");
        }
    }
}