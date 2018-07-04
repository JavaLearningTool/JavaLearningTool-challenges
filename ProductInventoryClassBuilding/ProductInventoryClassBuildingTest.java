import java.util.Map;
import java.util.HashMap;

public class ProductInventoryClassBuildingTest extends ClassTester {

    public static void main(String[] args) {
        new ProductInventoryClassBuildingTest();
    }

    public ProductInventoryClassBuildingTest() {
        super("ProductInventoryClassBuildingTest$ProductInventory", "ProductInventory");

        if (!didForm()) {
            printResults();
            return;
        }

        final Product[] products = { new Product("Quantum Computer", 5000.99), new Product("Mac book", 2110.99),
                new Product("Google Drive", 0) };

        final Product[] buyProd1 = { products[1], products[2] };
        final int[] buyAmount1 = { 2, 5 };
        final Product[] sellProd1 = { products[1] };
        final int[] sellAmount1 = { 1 };

        final Product[] buyProd2 = products;
        final int[] buyAmount2 = { 7, 5, 11 };
        final Product[] sellProd2 = { products[0], products[1] };
        final int[] sellAmount2 = { 1, 5 };

        final Product[] buyProd3 = { products[1], products[1], products[1] };
        final int[] buyAmount3 = { 7, 5, 11 };
        final Product[] sellProd3 = { products[1], products[1] };
        final int[] sellAmount3 = { 1, 5 };

        addPurchaseTest(buyProd1, sellProd1, buyAmount1, sellAmount1);
        addPurchaseTest(buyProd2, sellProd2, buyAmount2, sellAmount2);
        addPurchaseTest(buyProd3, sellProd3, buyAmount3, sellAmount3);

        runTests(200);

    }

    private void addPurchaseTest(Product[] purchaseProd, Product[] sellProd, int[] purchaseAmount, int[] sellAmount) {
        startGroup("Testing purchaseProduct and sellProduct");

        Map<Product, Integer> createdMap = new HashMap<Product, Integer>();

        addFormatConstructor("constructor", "ProductInventory %s = new ProductInventory();", "pI");
        for (int i = 0; i < purchaseProd.length; i++) {
            Product prod = purchaseProd[i];
            int which = i;
            if (!createdMap.containsKey(prod)) {
                addAction("Product p%d = new Product(\"%s\", %.2f);", i, prod.getName(), prod.getPrice());
                createdMap.put(prod, i);
            } else {
                which = createdMap.get(prod);
            }

            addChunk("purchaseProduct", "pI.purchaseProduct(p" + which + ", " + purchaseAmount[i] + ");", "pI", prod,
                    purchaseAmount[i]);
            addFieldTest("pI", "inventory");
        }

        for (int i = 0; i < sellProd.length; i++) {
            Product prod = sellProd[i];
            int which = i;
            if (!createdMap.containsKey(prod)) {
                addAction("Product p%d = new Product(\"%s\", %.2f);", i, prod.getName(), prod.getPrice());
                createdMap.put(prod, i);
            } else {
                which = createdMap.get(prod);
            }
            addChunk("sellProduct", "pI.sellProduct(p" + which + ", " + sellAmount[i] + ");", "pI", prod,
                    sellAmount[i]);
            addFieldTest("pI", "inventory");
        }
    }

    public static class ProductInventory {

        @TestedMember(equality = EqualityTester.OBJECT)
        private Map<Product, Integer> inventory = new HashMap<>();

        @TestedMember
        public ProductInventory() {
        }

        @TestedMember
        public void purchaseProduct(Product product, int amount) {
            if (inventory.containsKey(product)) {
                inventory.put(product, inventory.get(product) + amount);
            } else {
                inventory.put(product, amount);
            }
        }

        @TestedMember
        public void sellProduct(Product product, int amount) {
            inventory.put(product, inventory.get(product) - amount);
        }
    }
}