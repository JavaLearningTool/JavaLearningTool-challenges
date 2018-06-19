public class PaneraClassTester extends ClassTester {

    public PaneraClassTester() {
        super("PaneraClassTester$Panera", "Panera");

        setFieldLimit(0);

        if (!didForm()) {
            return;
        }

        startGroup("Testing constructor");
        addFormatConstructor("constructor", "Panera %s = new Panera(%.2f);", "panera", 4.0);
        addFieldTest("panera", "baseCost");
    }

    public static class Panera extends FastFoodClassTester.FastFood {
        @TestedMember
        public Panera(double baseCost) {
            super(baseCost);
        }

        public double costOfFood(int numPeople) {
            return (numPeople - 2) * 8.4 + getBaseCost();
        }
    }

}