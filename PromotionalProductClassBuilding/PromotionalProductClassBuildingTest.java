public class PromotionalProductClassBuildingTest extends ClassTester {

    public static void main(String[] args) {
        new PromotionalProductClassBuildingTest();
    }

    public PromotionalProductClassBuildingTest() {
        super("PromotionalProductClassBuildingTest$PromotionalProduct", "PromotionalProduct");

        if (!didForm()) {
            printResults();
            return;
        }

        final int[] amounts = { 4, 5, 2 };
        final double[] rates = { .2, .5, .75 };
        final Product[] products = { new Product("Quantum Computer", 5000.99), new Product("Mac book", 2110.99),
                new Product("Google Drive", 0) };

        addConstructorTest(products[0]);
        addConstructorTest(products[1]);

        addPromotionTest(products[0], rates[0], amounts[0]);
        addPromotionTest(products[1], rates[1], amounts[1]);
        addPromotionTest(products[2], rates[2], amounts[2]);

        runTests(200);

    }

    private void addConstructorTest(Product p) {
        String pName = p.getName();
        double pAmount = p.getPrice();

        addFieldTest("p", "name");
        addFieldTest("p", "price");
    }

    private void addPromotionTest(Product p, double rate, int amount) {
        String pName = p.getName();
        double pAmount = p.getPrice();
        startGroup("Testing Promotions.");
        addFormatConstructor("constructor", "PromotionalProduct %s = new PromotionalProduct(\"%s\", %.2f);", "p", pName,
                pAmount);
        addFormatChunk("setPromotionalRate", "%s.setPromotionalRate(%.2f);", "p", rate);
        addFormatChunk("getPriceOfAmount", "%s.getPriceOfAmount(%d);", "p", amount);
        addFormatChunk("toString", "%s.toString();", "p");

    }

    public static class PromotionalProduct extends Product {

        private double promotionRate = 1;

        @TestedMember
        public PromotionalProduct(String name, double price) {
            super(name, price);
        }

        @TestedMember
        public void setPromotionalRate(double rate) {
            promotionRate = rate;
        }

        @TestedMember(equality = EqualityTester.FLOATING_POINT)
        public double getPriceOfAmount(int amount) {
            return getPrice() * amount * promotionRate;
        }

        @TestedMember(equality = EqualityTester.OBJECT)
        public String toString() {
            return String.format("%s is selling for %.1f%% its regular price", getName(), promotionRate * 100);
        }
    }
}