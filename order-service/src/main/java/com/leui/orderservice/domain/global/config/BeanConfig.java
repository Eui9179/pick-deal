package com.leui.orderservice.domain.global.config;

import com.leui.orderservice.domain.payments.provider.PaymentProviderHandler;
import com.leui.orderservice.domain.payments.provider.toss.strategy.TossPaymentStrategy;
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
