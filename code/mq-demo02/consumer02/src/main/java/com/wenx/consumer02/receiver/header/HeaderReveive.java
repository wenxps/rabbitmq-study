package com.wenx.consumer02.receiver.header;

import com.wenx.consumer02.config.header.HeaderConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HeaderReveive {
    @RabbitListener(queues = HeaderConfig.HEADER_QUEUE_NAME_NAME)
    public void handleMsg1(byte[] message) throws IOException {
        System.out.println("HEADER-NAME消息内容1 = " + new String(message,0,message.length));
    }

    @RabbitListener(queues = HeaderConfig.HEADER_QUEUE_NAME_AGE)
    public void handleMsg2(byte[] message) throws IOException {
        System.out.println("HEADER-AGE消息内容2 = " + new String(message,0,message.length));
    }

}