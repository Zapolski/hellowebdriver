package com.epam.zapolski.purejavaconfig;

import com.epam.zapolski.mixconfig.Coach;
import com.epam.zapolski.mixconfig.FortuneService;
import com.epam.zapolski.mixconfig.HappyFortuneService;
import com.epam.zapolski.mixconfig.SwimCoach;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@Configurable
@PropertySource("classpath:sport.properties")
public class SportConfig {

    @Bean
    public FortuneService fortuneService() {
        return new HappyFortuneService();
    }


    @Bean
    public Coach swimCoach() {
        return new SwimCoach(fortuneService());
    }

}
