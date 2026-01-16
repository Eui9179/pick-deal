package com.leui.orderservice.domain.config.bean;

import com.leui.orderservice.domain.payments.strategy.PaymentProviderHandler;
import com.leui.orderservice.domain.payments.strategy.toss.TossPaymentStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BeanConfig {

    @Bean
    public PaymentProviderHandler paymentHandler(TossPaymentStrategy tossPayment) {
        return new PaymentProviderHandler(List.of(
                tossPayment
        ));
    }
}
