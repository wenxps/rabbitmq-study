package com.wenx.consumer02.config.ttl;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String JAVABOY_MESSAGE_DEALY_QUEUE_NAME = "javaboy_message_delay_queue_name";
    public static final String JAVABOY_MESSAGE_DELAY_EXCHANGE_NAME = "javaboy_message_delay_exchange_name";


    @Bean
    Queue messageQueue(){
        return new Queue(JAVABOY_MESSAGE_DEALY_QUEUE_NAME,true,false,false);
    }

    @Bean
    DirectExchange messageDirectExchange(){
        return new DirectExchange(JAVABOY_MESSAGE_DELAY_EXCHANGE_NAME,true,false);
    }

    @Bean
    Binding messageDelayBinding(){
        return BindingBuilder.bind(messageQueue())
                .to(messageDirectExchange())
                .with(JAVABOY_MESSAGE_DEALY_QUEUE_NAME);
    }
}