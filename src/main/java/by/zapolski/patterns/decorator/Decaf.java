package by.zapolski.patterns.decorator;

public class Decaf extends Beverage {
    public Decaf() {
        description = "Cafein Free";
    }
    public double cost() {
        return 1.05;
    }
}
