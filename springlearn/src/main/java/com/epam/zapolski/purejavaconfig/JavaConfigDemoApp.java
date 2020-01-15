package com.epam.zapolski.purejavaconfig;

import com.epam.zapolski.mixconfig.Coach;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class JavaConfigDemoApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(SportConfig.class);
        Coach coach = context.getBean("swimCoach", Coach.class);
        System.out.println(coach.getDailyWorkout());
        System.out.println(coach.getFortune());
        System.out.println(coach.getTeam());
        System.out.println(coach.getEmail());
        context.close();

    }
}
