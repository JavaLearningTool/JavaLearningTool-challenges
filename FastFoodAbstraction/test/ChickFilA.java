public class ChickFilA extends FastFood {
 	    
    public ChickFilA(double baseCost) {
        super(baseCost);
    }
    
    public double costOfFood(int numPeople) {
        return numPeople * 7.6 + getBaseCost();
    }
}