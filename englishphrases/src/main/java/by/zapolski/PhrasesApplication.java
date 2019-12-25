package by.zapolski;

import by.zapolski.filter.CORSFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(proxyBeanMethods = false)
public class PhrasesApplication {

    @Bean
    public FilterRegistrationBean commonsRequestLoggingFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(new CORSFilter());
        return registrationBean;
    }

    public static void main(String[] args) {
        SpringApplication.run(PhrasesApplication.class, args);
    }

}
