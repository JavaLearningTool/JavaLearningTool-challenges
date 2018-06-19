
public class FastFoodClassTester extends ClassTester {

    public FastFoodClassTester() {
        super("FastFoodClassTester$FastFood", "FastFood");

        if (!didForm()) {
            return;
        }
    }

    public static abstract class FastFood {
        @TestedMember(equality = EqualityTester.FLOATING_POINT)
        private double baseCost;

        public FastFood(double baseCost) {
            this.baseCost = baseCost;
        }

        public double getBaseCost() {
            return baseCost;
        }

        public abstract double costOfFood(int numPeople);

    }

}