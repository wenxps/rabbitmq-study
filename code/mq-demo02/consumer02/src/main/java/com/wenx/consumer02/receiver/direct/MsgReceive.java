package com.wenx.consumer02.receiver.direct;

import com.wenx.consumer02.config.direct.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MsgReceive {

    @RabbitListener(queues = RabbitConfig.DIRECT_QUEUE_NAME)
    public void handleMsg1(String message) throws IOException {
        System.out.println("消息内容1 = " + message);
    }

    @RabbitListener(queues = RabbitConfig.DIRECT_QUEUE_NAME2)
    public void handleMsg2(String message) throws IOException {
        System.out.println("消息内容2 = " + message);

    }

}