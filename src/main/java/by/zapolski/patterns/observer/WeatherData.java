package by.zapolski.patterns.observer;

import java.util.ArrayList;
import java.util.List;

public class WeatherData implements Subject{
    private List<MyObserver> observers;
    private float temperature;
    private float humidity;
    private float pressure;

    public WeatherData() {
        observers = new ArrayList<>();
    }

    @Override
    public void registerObserver(MyObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(MyObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        observers.forEach(myObserver -> myObserver.update(temperature, humidity, pressure));
    }

    public void measurementsChanged(){
        notifyObservers();
    }

    public void setMeasurements (float temperature, float humidity, float pressure) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        measurementsChanged();
    }
}
