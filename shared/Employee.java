/**
 * Employee class represents an employee.
 * This class is primarily made for extension but you may still instantiate
 * an Employee if you want.
 *
 * Employees have a name, wage, and vacation days
 */
public class Employee {
    private String name;
    private double wage;
    private int vacationDays;

    /**
     * Initializes an Employee object with the specified name. The Employee's
     * wage will be 8.5 and vacationDays will be 15.
     *
     * @param name  The Employee's name
     */
    public Employee(String name) {
        this(name, 8.5);
    }

    /**
     * Initializes an Employee object with the specified name and wage.
     * The Employee's vacationDays will be 15.
     *
     * @param name  The Employee's name
     * @param wage  The Employee's wage
     */
    public Employee(String name, double wage) {
        this(name, wage, 15);
    }

    /**
    * Initializes an Employee object with the specified name, wage, and
    * vacationDays.
    *
    * @param name  The Employee's name
    * @param wage  The Employee's wage
    * @param vacationDays   The Employee's vacationDays they have left
    */
    public Employee(String name, double wage, int vacationDays) {
        this.name = name;
        this.wage = wage;
        this.vacationDays = vacationDays;
    }

    /**
     * @return the name of this Employee
     */
    public String getName() {
        return name;
    }

    /**
     * @param name  The new name of this Employee
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the wage of this Employee
     */
    public double getWage() {
        return wage;
    }

    /**
     * @param wage  The new wage of this Employee
     */
    public void setWage(double wage) {
        this.wage = wage;
    }

    /**
     * @return The vacation days of this Employee
     */
    public int getVacationDays() {
        return vacationDays;
    }

    /**
     * This method will subtract numDays from vacationDays effectively using up
     * numDays worth of vacation days from the Employee
     *
     * @param numDays   The number of vacation days to take away from the
     * Employee
     */
    public void useVacationDays(int numDays) {
        vacationDays -= numDays;
    }

    /**
     * @return the String representatation of this Employee
     */
    public String toString() {
        return String.format("{name: %s, wage: %f, vacationDays: %d}", name, wage, vacationDays);
    }

    @Override
    /**
     * An Object is equal to this Employee if the Object is an instance of
     * Employee and all fields are equal
     * 
     * @param other The object to test equality with
     * @return true if the Object is equal to this, false otherwise
     */
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Employee)) {
            return false;
        }

        Employee oEmp = (Employee) other;

        return this.name.equals(oEmp.name) && this.wage == oEmp.wage && this.vacationDays == oEmp.vacationDays;
    }
}