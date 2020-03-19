package by.zapolski.patterns.decorator;

public class DarkRoast extends Beverage {
    public DarkRoast() {
        description = "Dark roast";
    }
    public double cost() {
        return .89;
    }
}
