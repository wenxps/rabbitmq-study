package com.wenx.consumer02.receiver.topic;

import com.wenx.consumer02.config.topic.TopicConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TopicReveive {
    @RabbitListener(queues = TopicConfig.XIAOMI_QUEUE_NAME)
    public void handleMsg1(String message) throws IOException {
        System.out.println("TOPIC消息内容1 = " + message);
    }

    @RabbitListener(queues = TopicConfig.HUAWEI_QUEUE_NAME)
    public void handleMsg2(String message) throws IOException {
        System.out.println("TOPIC消息内容2 = " + message);
    }

    @RabbitListener(queues = TopicConfig.PHONE_QUEUE_NAME)
    public void handleMsg3(String message) throws IOException {
        System.out.println("TOPIC消息内容3 = " + message);
    }
}