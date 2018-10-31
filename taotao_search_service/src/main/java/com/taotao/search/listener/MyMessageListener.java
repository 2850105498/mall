package com.taotao.search.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:接收ActiveMQ发送的消息
 * @author:cxg
 * @Date:${time}
 */
public class MyMessageListener implements MessageListener{
    @Override
    public void onMessage(Message message) {
        TextMessage textMessage= (TextMessage) message;
        try {
            String text = textMessage.getText();
            System.out.println(text);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
