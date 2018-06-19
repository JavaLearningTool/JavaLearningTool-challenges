
public class ChickFilAClassTester extends ClassTester {

    public ChickFilAClassTester() {
        super("ChickFilAClassTester$ChickFilA", "ChickFilA");

        setFieldLimit(0);

        if (!didForm()) {
            return;
        }

        startGroup("Testing constructor");
        addFormatConstructor("constructor", "ChickFilA %s = new ChickFilA(%.2f);", "chick", 4.0);
        addFieldTest("chick", "baseCost");
    }

    public static class ChickFilA extends FastFoodClassTester.FastFood {

        @TestedMember
        public ChickFilA(double baseCost) {
            super(baseCost);
        }

        public double costOfFood(int numPeople) {
            return numPeople * 7.6 + getBaseCost();
        }
    }
}