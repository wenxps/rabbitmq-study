package com.wenx.consumer02.receiver.fanout;

import com.wenx.consumer02.config.fanout.FanoutConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FanoutReceive {

    @RabbitListener(queues = FanoutConfig.FANOUT_QUEUE_NAME)
    public void handleMsg1(String message) throws IOException {
        System.out.println("FANOUT消息内容1 = " + message);
    }

    @RabbitListener(queues = FanoutConfig.FANOUT_QUEUE_NAME2)
    public void handleMsg2(String message) throws IOException {
        System.out.println("FANOUT消息内容2 = " + message);
    }

}