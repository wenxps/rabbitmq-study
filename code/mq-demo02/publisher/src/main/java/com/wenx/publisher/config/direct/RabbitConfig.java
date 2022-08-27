package com.wenx.publisher.config.direct;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Direct:这种路由策略，将消息队列绑定到 DirectExchange 上，
 * 当消息到达交换机的时候，消息会携带 routing_key,
 * 交换机会找到名为 routing_key 的队列，将消息路由过去
 * @author 温笙
 */
@Configuration
public class RabbitConfig {

    public static final String DIRECT_QUEUE_NAME = "direct_queue_name";
    public static final String DIRECT_QUEUE_NAME2 = "direct_queue_name2";

    public static final String DIRECT_EXCHANGE_NAME = "direct_exchange_name";


    @Bean
    Queue directQueue1(){
        return new Queue(DIRECT_QUEUE_NAME,true,false,false);
    }

    @Bean
    Queue directQueue2(){
        return new Queue(DIRECT_QUEUE_NAME2,true,false,false);
    }

    /**
     * 定义一个直连交换机
     * @return
     */
    @Bean
    DirectExchange directExchange(){
        //1.交换机名称
        //2.交换机本身是否持久化
        //3.如果没有与之绑定的队列，是否删除交换机
        return new DirectExchange(DIRECT_EXCHANGE_NAME,true,false);
    }

    @Bean
    Binding directBinding1(){
        return BindingBuilder
                //设置绑定的队列
                .bind(directQueue1())
                //设置绑定的交换机
                .to(directExchange())
                //设置 routing_key
                .with(DIRECT_QUEUE_NAME);
    }

    @Bean
    Binding directBinding2(){
        return BindingBuilder
                //设置绑定的队列
                .bind(directQueue2())
                //设置绑定的交换机
                .to(directExchange())
                //设置 routing_key
                .with(DIRECT_QUEUE_NAME2);
    }

}