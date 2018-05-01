/**
 * Product class represents a product.
 * This class is primarily made for extension but you may still instantiate
 * a Product if you want.
 *
 * Products have a name, id, and price
 */
public class Product {

    private String name;
    private double price;

    private final long id;

    /**
     * Initializes a Product with the given name and price. The product is
     * assigned an id
     * 
     * @param name  The Product's name
     * @param price The Product's price
     */
    public Product(String name, double price) {
        this.name = name;
        this.price = price;
        id = name.length() + (long) price;
    }

    /**
     * Returns the price for the specified amount of this product
     * 
     * @param amount    The amount of the product
     * @return The price of amount units of this product
     */
    public double getPriceOfAmount(int amount) {
        return price * amount;
    }

    /**
     * @return the name of the product
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name  the new name of the product
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the price of the product
     */
    public double getPrice() {
        return this.price;
    }

    /**
     * @param price the new price of the product
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * @return the id of the product
     */
    public long getId() {
        return this.id;
    }

    /**
     * @return the String representation of this Product
     */
    public String toString() {
        return String.format("{name: %s, price: %.2f, id: %d}", name, price, id);
    }

    @Override
    /**
     * An Object is equal to this Product if the Object is an instance of
     * Product and all fields are equal
     * 
     * @param other The object to test equality with
     * @return true if the Object is equal to this, false otherwise
     */
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Product)) {
            return false;
        }

        Product oEmp = (Product) other;

        return this.name.equals(oEmp.name) && this.price == oEmp.price && this.id == oEmp.id;
    }
}