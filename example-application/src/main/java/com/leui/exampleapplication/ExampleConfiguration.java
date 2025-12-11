package com.leui.exampleapplication;

import com.leui.HelloPrinter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExampleConfiguration {
    @Bean
    public HelloPrinter helloPrinter() {
        return new HelloPrinter();
    }
}
