public class Test {

    public static void main(String[] args) {
        int num = Integer.parseInt(args[0].substring(0, 1));
        switch(num) {
            case 0:
                System.out.println("Invalid.");
                break;
            case 1:
                System.out.println("Company calling.");
                break;
            case 2:
            case 3:
                System.out.println("Where is this number from?");
            default:
                System.out.println("Patching you through.");
        }
    }
}