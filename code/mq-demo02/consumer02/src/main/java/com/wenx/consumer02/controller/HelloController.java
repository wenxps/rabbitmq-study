package com.wenx.consumer02.controller;

import com.wenx.consumer02.config.ttl.RabbitConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//public class HelloController {
//
//    @Autowired
//    RabbitTemplate rabbitTemplate;
//
//    @GetMapping("/send")
//    public void hello(){
//        Message build = MessageBuilder.withBody("hello javaboy".getBytes())
//                // 设置过期时间 10s 消费达到 RabbitMQ 10s 内还没有人消费，消息就会过期
//                .setExpiration("10000")
//                .build();
//
//        rabbitTemplate.send(RabbitConfig.JAVABOY_MESSAGE_DELAY_EXCHANGE_NAME,RabbitConfig.JAVABOY_MESSAGE_DEALY_QUEUE_NAME, build);
//    }
//}

@RestController
public class HelloController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/send")
    public void hello(){
        rabbitTemplate.convertAndSend(RabbitConfig.JAVABOY_MESSAGE_DELAY_EXCHANGE_NAME, RabbitConfig.JAVABOY_QUEUE_DEMO, "hello");
    }
}