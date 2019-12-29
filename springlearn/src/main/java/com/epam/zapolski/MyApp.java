package com.epam.zapolski;

public class MyApp {
    public static void main(String[] args) {
        Coach theCoach = new BaseballCoach();
        System.out.println(theCoach.getDailyWorkout());
    }
}
