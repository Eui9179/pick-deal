package com.leui.orderservice.global.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leui.orderservice.domain.payments.provider.PaymentProviderHandler;
import com.leui.orderservice.domain.payments.provider.kakao.strategy.KakaoPaymentStrategy;
import com.leui.orderservice.domain.payments.provider.toss.strategy.TossPaymentStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.RedisRepository;

import java.util.List;

@Configuration
public class BeanConfig {

    @Bean
    public PaymentProviderHandler paymentHandler(
            TossPaymentStrategy tossPayment,
            KakaoPaymentStrategy kakaoPayment
    ) {
        return new PaymentProviderHandler(List.of(tossPayment, kakaoPayment));
    }

}
