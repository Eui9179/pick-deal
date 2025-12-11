package com.leui.exampleapplication;

import com.leui.HelloPrinter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class ExampleApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ExampleApplication.class, args);
        HelloPrinter printer = context.getBean(HelloPrinter.class);
        printer.print();
    }

}
