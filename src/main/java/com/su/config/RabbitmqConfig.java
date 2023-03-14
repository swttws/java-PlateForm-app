package com.su.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitmqConfig {

    public static final String DELAY_EXCHANGE="delay_exchange";
    public static final String DELAY_QUEUE="delay_queue";
    public static final String DELAY_KEY="delay";

    @Bean
    public Queue delayQueue(){
        return QueueBuilder.durable(DELAY_QUEUE).build();
    }

    //延迟交换机
    @Bean
    public CustomExchange customExchange(){
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(DELAY_EXCHANGE,"x-delayed-message", true, false,args);
    }

    @Bean
    public Binding binding(){
        return BindingBuilder.bind(delayQueue())
                .to(customExchange())
                .with(DELAY_KEY)
                .noargs();
    }

}
