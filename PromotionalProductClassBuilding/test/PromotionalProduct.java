public class PromotionalProduct extends Product {

    private double promotionRate = 1;

    public PromotionalProduct(String name, double price) {
        super(name, price);
    }

    public void setPromotionalRate(double rate) {
        promotionRate = rate;
    }

    public double getPriceOfAmount(int amount) {
        return getPrice() * amount * promotionRate;
    }

    public String toString() {
        return String.format("%s is selling for %.1f%% its regular price", getName(), promotionRate * 100);
    }
}