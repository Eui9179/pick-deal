package com.leui.orderservice.global.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leui.orderservice.domain.order.service.OrderService;
import com.leui.orderservice.domain.payments.provider.PaymentProviderHandler;
import com.leui.orderservice.domain.payments.provider.toss.strategy.TossPaymentStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

@Configuration
public class BeanConfig {

    @Bean
    public PaymentProviderHandler paymentHandler(TossPaymentStrategy tossPayment) {
        return new PaymentProviderHandler(List.of(tossPayment));
    }


    @Bean
    public StringRedisTemplate redisTemplate(RedisConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }

}
