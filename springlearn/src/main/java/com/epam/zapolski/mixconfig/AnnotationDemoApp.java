package com.epam.zapolski.mixconfig;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AnnotationDemoApp {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("mix-applicationContext.xml");
        Coach coach = context.getBean("baseballCoach", Coach.class);
        System.out.println(coach.getDailyWorkout());
        System.out.println(coach.getFortune());
        context.close();

    }
}
