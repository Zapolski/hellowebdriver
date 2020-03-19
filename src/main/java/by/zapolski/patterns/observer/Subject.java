package by.zapolski.patterns.observer;

public interface Subject {
    void registerObserver(MyObserver observer);
    void removeObserver(MyObserver observer);
    void notifyObservers();
}
