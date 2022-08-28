package com.wenx.consumer02.config.rpc;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String RPC_QUEUE1 = "queue_1";
    public static final String RPC_QUEUE2 = "queue_2";
    public static final String RPC_EXCHANGE = "rpc_exchange";

    /**
     * 配置消息发送队列
     */
    @Bean
    Queue msgQueue() {
        return new Queue(RPC_QUEUE1);
    }

    /**
     * 设置返回队列
     */
    @Bean
    Queue replyQueue() {
        return new Queue(RPC_QUEUE2);
    }

    /**
     * 设置交换机
     */
    @Bean
    TopicExchange exchange() {
        return new TopicExchange(RPC_EXCHANGE);
    }

    /**
     * 请求队列和交换器绑定
     */
    @Bean
    Binding msgBinding() {
        return BindingBuilder.bind(msgQueue()).to(exchange()).with(RPC_QUEUE1);
    }

    /**
     * 返回队列和交换器绑定
     */
    @Bean
    Binding replyBinding() {
        return BindingBuilder.bind(replyQueue()).to(exchange()).with(RPC_QUEUE2);
    }
}