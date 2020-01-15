package com.epam.zapolski.mixconfig;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class HappyFortuneService implements FortuneService {

    @Override
    public String getFortune() {
        return "A good day!";
    }

    @PostConstruct
    public void postConstruct() {
        System.out.println("Inside postConstruct");
    }

    @PreDestroy
    public void preDestroy() {
        System.out.println("Inside preDestroy()");
    }


}
