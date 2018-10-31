package com.taotao.activemq;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:整合spring和activemq
 * @author:cxg
 * @Date:${time}
 */
public class SpringActivemq {

    //使用jsmTemplate发送消息
    @Test
    public void testJmsTemplate() throws Exception {
        //初始化spring容器
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("classpath:spring/applicationContext-activemq.xml");
        //从容器中获得JmsTemplate对象
        JmsTemplate  jmsTemplate= applicationContext.getBean(JmsTemplate.class);
        //从容器中获得Destination对象
         Queue queue= (Queue) applicationContext.getBean("test-queue");
        //发送消息
        jmsTemplate.send(queue, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                TextMessage textMessage = session.createTextMessage("我们10,2");
                return textMessage;
            }
        });


    }
}
