package by.zapolski;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class PhrasesApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhrasesApplication.class, args);
    }

}
