package com.wenx.consumer.receiver;

import com.wenx.consumer.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消费者
 */
@Component
public class MsgReceiver {

    /**
     * @RabbitListener:指定该方法监听的消息队列
     * @param msg 消息
     */
    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void handleMsg(String msg){
        System.out.println(msg);
    }
}