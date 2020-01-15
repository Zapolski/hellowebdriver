package com.epam.zapolski.xmlconfig;

import lombok.Data;

@Data
public class CricketCoach implements Coach {

    private FortuneService fortuneService;
    private String emailAddress;
    private String team;

    @Override
    public String getDailyWorkout() {
        return "Practice fast bowling for 15 minutes";
    }

    @Override
    public String getDailyFortune() {
        return fortuneService.getFortune();
    }

    @Override
    public void getInfo() {
        System.out.println("Email: " + emailAddress + "; team: " + team);
    }

    public void initMethod() {
        System.out.println("CricketCoach: inside init method.");
    }

    public void destroyMethod() {
        System.out.println("CricketCoach: inside destroy method.");
    }
}
