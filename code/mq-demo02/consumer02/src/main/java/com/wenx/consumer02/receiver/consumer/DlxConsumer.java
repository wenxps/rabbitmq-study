package com.wenx.consumer02.receiver.consumer;

import com.wenx.consumer02.config.dlx.RabbitDlxConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DlxConsumer {

    /**
     * 给 正常消息队列发送消息，由于消息队列没有消费者就会过期，就会发送带死信队列中，消费死信队列中的消息
     * @param msg
     */
    @RabbitListener(queues = RabbitDlxConfig.DLX_QUEUE_NAME)
    public void handle(String msg){
        System.out.println("msg = " + msg);
    }
}