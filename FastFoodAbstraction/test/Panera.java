public class Panera extends FastFood {
 	    
    public Panera(double baseCost) {
        super(baseCost);
    }
    
    public double costOfFood(int numPeople) {
        return (numPeople - 2) * 8.4 + getBaseCost();
    }
}