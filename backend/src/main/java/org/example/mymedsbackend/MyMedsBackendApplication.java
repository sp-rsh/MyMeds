package org.example.mymedsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MyMedsBackendApplication {

    static void main(String[] args) {
        SpringApplication.run(MyMedsBackendApplication.class, args);
    }

}
