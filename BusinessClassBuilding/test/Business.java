public class Business {

    private String name;

    private Product product;

    private int currentStock = 10;

    public Business(String name) {
        this.name = name;
    }

    public void setProduct(Product p) {
        product = p;
    }

    public void inventNewProduct(String productName, double price) {
        product = new Product(productName, price);
    }

    public double sellItems(int numItems) {
        if (currentStock < numItems) {
            numItems = currentStock;
        }

        currentStock -= numItems;

        return product.getPriceOfAmount(numItems);
    }

    public void buyProductStock(double moneySpent) {
        currentStock += (int) (moneySpent / product.getPrice());
    }

    @Override
    public String toString() {
        return String.format("%s sells %s.", name, product.getName());
    }

    @Override
    public boolean equals(Object other) {
        return ClassTester.tester.fieldsEqual(this, other);
    }

}