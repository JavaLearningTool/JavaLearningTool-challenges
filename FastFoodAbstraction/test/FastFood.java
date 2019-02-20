public abstract class FastFood {
    // Your code here
   private double baseCost;
   
   public FastFood(double baseCost) {
       this.baseCost = baseCost;
   }
   
   public double getBaseCost() {
       return baseCost;
   }
   
   public abstract double costOfFood(int numPeople);
  
}