package com.wenx.consumer02.config.dlx;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitDlxConfig {
    public static final String DLX_EXCHANGE_NAME = "dlx_exchange_name";
    public static final String DLX_QUEUE_NAME = "dlx_queue_name";
    public static final String DLX_ROUTING_KEY = "dlx_routing_key";

    /**
     * 配置死信交换机
     *
     * @return
     */
    @Bean
    DirectExchange dlxDirectExchange() {
        return new DirectExchange(DLX_EXCHANGE_NAME, true, false);
    }
    /**
     * 配置死信队列
     * @return
     */
    @Bean
    Queue dlxQueue() {
        return new Queue(DLX_QUEUE_NAME);
    }
    /**
     * 绑定死信队列和死信交换机
     * @return
     */
    @Bean
    Binding dlxBinding() {
        return BindingBuilder.bind(dlxQueue())
                .to(dlxDirectExchange())
                .with(DLX_ROUTING_KEY);
    }
}