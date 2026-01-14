package com.leui.orderservice.domain.config.bean;

import com.leui.orderservice.domain.payments.strategy.PaymentHandler;
import com.leui.orderservice.domain.payments.strategy.kakao.KakaoPaymentStrategy;
import com.leui.orderservice.domain.payments.strategy.toss.TossPaymentStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class BeanConfig {

    @Bean
    public PaymentHandler paymentHandler() {
        return new PaymentHandler(List.of(
                new TossPaymentStrategy(),
                new KakaoPaymentStrategy()
        ));
    }
}
