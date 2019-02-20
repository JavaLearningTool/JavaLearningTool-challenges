import java.util.Map;
import java.util.HashMap;

public class ProductInventory {

    private Map<Product, Integer> inventory = new HashMap<>();

    public ProductInventory() {
    }

    public void purchaseProduct(Product product, int amount) {
        if (inventory.containsKey(product)) {
            inventory.put(product, inventory.get(product) + amount);
        } else {
            inventory.put(product, amount);
        }
    }

    public void sellProduct(Product product, int amount) {
        inventory.put(product, inventory.get(product) - amount);
    }
}