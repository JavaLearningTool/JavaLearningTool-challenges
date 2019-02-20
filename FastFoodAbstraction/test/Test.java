public class Test {
    public double eatAt(FastFood ff, double moneyInMyWallet, int numPeople) {
        return moneyInMyWallet - ff.costOfFood(numPeople);
    }
}