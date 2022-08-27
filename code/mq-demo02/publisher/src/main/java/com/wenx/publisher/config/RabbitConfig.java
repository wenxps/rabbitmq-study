package com.wenx.publisher.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    // 队列名称
    public static final String HELLO_WORLD_QUEUE_NAME = "hello_world_queue";

    /**
     * 1.第一个参数是队列名称
     * 2.第二个参数是持久化
     * 3.该队列是否具有排他性，有排他性的队列只能被创建其的 Connection 处理
     * 4.如果该队列没有消费者是否删除
     * @return
     */
    @Bean
    Queue wenxQueue(){
        return new Queue(HELLO_WORLD_QUEUE_NAME,true,false,false);
    }
}