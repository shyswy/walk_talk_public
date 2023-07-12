package com.example.clubsite;

import com.example.clubsite.aop.aspect.TraceAspect;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = {"com.example.clubsite.repository"})
//@Import(TraceAspect.class)
public class ClubSiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClubSiteApplication.class, args);
    }

}
