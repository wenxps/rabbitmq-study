package com.wenx.publisher;

import com.wenx.publisher.config.RabbitConfig;
import com.wenx.publisher.config.fanout.FanoutConfig;
import com.wenx.publisher.config.header.HeaderConfig;
import com.wenx.publisher.config.topic.TopicConfig;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.charset.StandardCharsets;

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

    @Test
    void fanout() {
        rabbitTemplate.convertAndSend(FanoutConfig.FANOUT_EXCHANGE_NAME,null,"这条消息发给队列");
    }

    @Test
    void topic() {
        rabbitTemplate.convertAndSend(TopicConfig.TOPIC_EXCHANGE_NAME,"xiaomi.news","小米新闻");
        rabbitTemplate.convertAndSend(TopicConfig.TOPIC_EXCHANGE_NAME,"huawei.news","华为");
        rabbitTemplate.convertAndSend(TopicConfig.TOPIC_EXCHANGE_NAME,"huawei.phone.news","华为手机新闻");
    }

    @Test
    void header() {
        Message nameMsg = MessageBuilder.withBody("hello zhangsan".getBytes(StandardCharsets.UTF_8)).setHeader("name", "aaa").build();
        Message ageMsg = MessageBuilder.withBody("hello lisi".getBytes(StandardCharsets.UTF_8)).setHeader("age", 99).build();
        rabbitTemplate.send(HeaderConfig.HEADER_EXCHANGE_NAME,null,nameMsg);
        rabbitTemplate.send(HeaderConfig.HEADER_EXCHANGE_NAME,null,ageMsg);
    }

}