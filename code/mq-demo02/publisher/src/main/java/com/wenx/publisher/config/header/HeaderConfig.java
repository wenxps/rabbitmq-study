package com.wenx.publisher.config.header;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 根据消息的头信息，来确定去哪个队列
 */
@Configuration
public class HeaderConfig {

    public static final String HEADER_QUEUE_NAME_NAME = "header_queue_name_name";
    public static final String HEADER_QUEUE_NAME_AGE = "header_queue_name_age";

    public static final String HEADER_EXCHANGE_NAME = "header_exchange_name";

    @Bean
    Queue headerQueue1(){
        return new Queue(HEADER_QUEUE_NAME_NAME,true,false,false);
    }

    @Bean
    Queue headerQueue2(){
        return new Queue(HEADER_QUEUE_NAME_AGE,true,false,false);
    }


    @Bean
    HeadersExchange headerExchange(){
        return new HeadersExchange(HEADER_EXCHANGE_NAME,true,false);
    }

    @Bean
    Binding headerBing1(){
        return BindingBuilder.bind(headerQueue1())
                .to(headerExchange())
                //如果将来消息中包含 name 属性，就算匹配成功
                .where("name").exists();
    }

    @Bean
    Binding headerBing2(){
        return BindingBuilder.bind(headerQueue2())
                .to(headerExchange())
                //如果将来消息中包含 age 属性并且等于 99，就算匹配成功
                .where("age").matches(99);
    }

}