package com.leui.orderservice.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leui.orderservice.domain.order.service.OrderService;
import com.leui.orderservice.domain.payments.provider.PaymentProviderHandler;
import com.leui.orderservice.domain.payments.provider.toss.strategy.TossPaymentStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BeanConfig {

    @Bean
    public PaymentProviderHandler paymentHandler(ObjectMapper objectMapper, TossPaymentStrategy tossPayment) {
        return new PaymentProviderHandler(objectMapper, List.of(tossPayment));
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
