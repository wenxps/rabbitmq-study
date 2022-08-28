package com.wenx.consumer02.config.dlx;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.wenx.consumer02.config.dlx.RabbitDlxConfig.DLX_EXCHANGE_NAME;
import static com.wenx.consumer02.config.dlx.RabbitDlxConfig.DLX_ROUTING_KEY;

@Configuration
public class RabbitConfig {
    public static final String MSG_EXCHANGE_NAME = "msg_exchange_name";
    public static final String MSG_QUEUE_NAME = "msg_queue_name";
    public static final String MSG_ROUTING_KEY = "msg_routing_key";

    /**
     * 配置普通交换机
     *
     * @return
     */
    @Bean
    DirectExchange msgDirectExchange() {
        return new DirectExchange(MSG_EXCHANGE_NAME, true, false);
    }
    /**
     * 配置普通队列
     * @return
     */
    @Bean
    Queue Queue() {
        Map<String, Object> args = new HashMap<>();
        //设置消息过期时间
        args.put("x-message-ttl", 0);
        //设置死信交换机
        args.put("x-dead-letter-exchange", DLX_EXCHANGE_NAME);
        //设置死信 routing_key
        args.put("x-dead-letter-routing-key", DLX_ROUTING_KEY);
        return new Queue(MSG_QUEUE_NAME,true,false,false,args);
    }
    /**
     * 绑定普通队列和普通交换机
     * @return
     */
    @Bean
    Binding msgBinding() {
        return BindingBuilder.bind(Queue())
                .to(msgDirectExchange())
                .with(MSG_ROUTING_KEY);
    }
}