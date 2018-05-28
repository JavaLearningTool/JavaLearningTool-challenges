public class BusinessClassBuildingTest extends ClassTester {

    public static void main(String[] args) {
        new BusinessClassBuildingTest();
    }

    public BusinessClassBuildingTest() {
        super("BusinessClassBuildingTest$Business", "Business");

        if (!didForm()) {
            printResults();
            return;
        }

        final String[] names = { "IBM", "Apple", "Google" };
        final Product[] products = { new Product("Quantum Computer", 5000.99), new Product("Mac book", 2110.99),
                new Product("Google Drive", 0) };

        addConstructorTest(names[0]);
        addConstructorTest(names[1]);

        addSetProduct(names[0], products[0]);
        addSetProduct(names[2], products[2]);

        addInventNewProduct(names[0], products[0].getName(), products[0].getPrice());
        addInventNewProduct(names[1], products[1].getName(), products[1].getPrice());

        sellItems(names[1], 2, products[0]);
        sellItems(names[1], 7, products[1]);
        sellItems(names[2], 15, products[2]);

        buyProductStock(names[0], 25.2, products[0]);
        buyProductStock(names[1], 701, products[1]);
        buyProductStock(names[2], 1, products[0]);

        addToStringTest(names[0], products[0]);
        addToStringTest(names[2], products[2]);

        runTests(200);
    }

    private void addSetProduct(String name, Product p) {
        String pName = p.getName();
        double pAmount = p.getPrice();
        startGroup("Testing setProduct.");
        addFormatConstructor("constructor", "Business %s = new Business(\"%s\");", "b", name);
        addAction("Product p = new Product(\"%s\", %.2f);", pName, pAmount);
        addChunk("setProduct", "b.setProduct(p);", "b", p);
        addFieldTest("b", "product");
    }

    private void addInventNewProduct(String name, String pName, double pPrice) {
        startGroup("Testing inventNewProduct.");
        addFormatConstructor("constructor", "Business %s = new Business(\"%s\");", "b", name);
        addFormatChunk("inventNewProduct", "%s.inventNewProduct(\"%s\", %.2f);", "b", pName, pPrice);
        addFieldTest("b", "product");
    }

    private void sellItems(String name, int amount, Product p) {
        String pName = p.getName();
        double pAmount = p.getPrice();
        startGroup("Testing sellItems.");
        addFormatConstructor("constructor", "Business %s = new Business(\"%s\");", "b", name);
        addAction("Product p = new Product(\"%s\", %.2f);", pName, pAmount);
        addChunk("setProduct", "b.setProduct(p);", "b", p);
        addFormatChunk("sellItems", "%s.sellItems(%d);", "b", amount);
        addFieldTest("b", "currentStock");
    }

    private void buyProductStock(String name, double amount, Product p) {
        String pName = p.getName();
        double pAmount = p.getPrice();
        startGroup("Testing buyProductStock.");
        addFormatConstructor("constructor", "Business %s = new Business(\"%s\");", "b", name);
        addAction("Product p = new Product(\"%s\", %.2f);", pName, pAmount);
        addChunk("setProduct", "b.setProduct(p);", "b", p);
        addFormatChunk("buyProductStock", "%s.buyProductStock(%.2f);", "b", amount);
        addFieldTest("b", "currentStock");
    }

    private void addToStringTest(String name, Product p) {
        String pName = p.getName();
        double pAmount = p.getPrice();
        startGroup("Testing toString.");
        addFormatConstructor("constructor", "Business %s = new Business(\"%s\");", "b", name);
        addAction("Product p = new Product(\"%s\", %.2f);", pName, pAmount);
        addChunk("setProduct", "b.setProduct(p);", "b", p);
        addChunk("toString", "b.toString();", "b");
    }

    private void addConstructorTest(String name) {
        startGroup("Testing constructor.");
        addFormatConstructor("constructor", "Business %s = new Business(\"%s\");", "b", name);
        addFieldTest("b", "name");
        addFieldTest("b", "currentStock");
        addFieldTest("b", "product");
    }

    public static class Business {

        @TestedMember(equality = EqualityTester.OBJECT)
        private String name;

        @TestedMember(equality = EqualityTester.OBJECT)
        private Product product;

        @TestedMember(equality = EqualityTester.OBJECT)
        private int currentStock = 10;

        @TestedMember
        public Business(String name) {
            this.name = name;
        }

        @TestedMember
        public void setProduct(Product p) {
            product = p;
        }

        @TestedMember
        public void inventNewProduct(String productName, double price) {
            product = new Product(productName, price);
        }

        @TestedMember(equality = EqualityTester.FLOATING_POINT)
        public double sellItems(int numItems) {
            if (currentStock < numItems) {
                numItems = currentStock;
            }

            currentStock -= numItems;

            return product.getPriceOfAmount(numItems);
        }

        @TestedMember
        public void buyProductStock(double moneySpent) {
            currentStock += (int) (moneySpent / product.getPrice());
        }

        @Override
        @TestedMember(equality = EqualityTester.OBJECT)
        public String toString() {
            return String.format("%s sells %s.", name, product.getName());
        }

        @Override
        public boolean equals(Object other) {
            return ClassTester.tester.fieldsEqual(this, other);
        }

    }
}