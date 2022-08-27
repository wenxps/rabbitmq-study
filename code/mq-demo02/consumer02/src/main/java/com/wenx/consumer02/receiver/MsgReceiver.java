package com.wenx.consumer02.receiver;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.wenx.consumer02.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class MsgReceiver {

//    @RabbitListener(queues = RabbitConfig.HELLO_WORLD_QUEUE_NAME)
//    public void handleMsg1(String msg){
//        System.out.println("msg1:"+msg);
//    }
//
//    /**
//     * concurrency:并发数，即消费者将开启20个子线程消费消息
//     * @param msg
//     */
//    @RabbitListener(queues = RabbitConfig.HELLO_WORLD_QUEUE_NAME,concurrency = "20")
//    public void handleMsg2(String msg){
//        System.out.println("msg2:"+msg);
//    }

    @RabbitListener(queues = RabbitConfig.HELLO_WORLD_QUEUE_NAME, concurrency = "10")
    public void receive1(Message message, Channel channel) throws IOException {
        System.out.println("receive2 = " + message.getPayload() + "------->" + Thread.currentThread().getName());
        channel.basicReject(((Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG)), true);
    }

    @RabbitListener(queues = RabbitConfig.HELLO_WORLD_QUEUE_NAME, concurrency = "10")
    public void receive2(Message message, Channel channel) throws IOException {
        System.out.println("receive2 = " + message.getPayload() + "------->" + Thread.currentThread().getName());
        channel.basicReject(((Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG)), true);
    }
}