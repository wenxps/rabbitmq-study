package com.wenx.publisher;

import com.wenx.publisher.config.RabbitConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PublisherApplicationTests {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void contextLoads() {
        rabbitTemplate.convertAndSend(RabbitConfig.HELLO_WORLD_QUEUE_NAME,"你好 wenx");
    }

    @Test
    void contextLoads2() {
        for (int i = 0; i < 20; i++) {
            rabbitTemplate.convertAndSend(RabbitConfig.HELLO_WORLD_QUEUE_NAME,"你好 wenx"+i);
        }
    }

}