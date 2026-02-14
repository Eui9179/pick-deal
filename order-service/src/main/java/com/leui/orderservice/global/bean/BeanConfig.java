package com.leui.orderservice.global.bean;

import com.leui.orderservice.domain.payments.provider.PaymentProviderHandler;
import com.leui.orderservice.domain.payments.provider.kakao.strategy.KakaoPaymentStrategy;
import com.leui.orderservice.domain.payments.provider.toss.strategy.TossPaymentStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;

@Configuration
public class BeanConfig {

    @Bean
    public PaymentProviderHandler paymentHandler(
            KafkaTemplate<String, Object> kafkaTemplate,
            TossPaymentStrategy tossPayment,
            KakaoPaymentStrategy kakaoPayment
    ) {
        return new PaymentProviderHandler(kafkaTemplate,
                List.of(tossPayment, kakaoPayment)
        );
    }

}
