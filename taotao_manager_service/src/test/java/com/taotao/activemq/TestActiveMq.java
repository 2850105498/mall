package com.taotao.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.Test;

import javax.jms.*;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:ActiveMq测试
 * @author:cxg
 * @Date:${time}
 */
public class TestActiveMq {
    /**
     * 生产者
     *
     * @throws Exception
     */
    @Test
    public void testQueueProducer() throws Exception {
        //1.首先创建一个连接工厂对象，Connect
        // ionFactory对象，需要制定的mq服务的ip及端口号
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.129:61616");
        //2.使用ConnectionFactory创建一个连接Connection对象
        Connection connection = connectionFactory.createConnection();
        //3.开启连接，调用Connection对象的start方法
        connection.start();
        //4.使用Connection对象创建一个session
        //第一个参数，是否开启事务，一般不使用事务。保证数据的最终一致，可以使用消息队列
        // 如果第一个参数为true，第二个参数自动忽略。
        //如果不开启事务false，第二个参数为消息的应答模式，一般是自动应答
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //5.使用Session对象创建一个Destination对象，两种形式queue、topic，在这里使用queue
        Queue queue = session.createQueue("test-queue");
        //6.使用Session对象创建一个Producer对象
        MessageProducer producer = session.createProducer(queue);
        //7.创建一个TextMessage消息对象
/*        TextMessage textMessage=new ActiveMQTextMessage();
        textMessage.setText("hello activemq");*/
        TextMessage textMessage = session.createTextMessage("hello activemq");
        //8.发送消息
        producer.send(textMessage);
        //9.关闭资源
        producer.close();
        session.close();
        connection.close();
    }

    /**
     * 消费者接收消息
     * @throws Exception
     */
    @Test
    public void testQueueConsumer() throws Exception {
        //创建一个连接工厂
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.25.129:61616");
        //使用连接工厂创建一个连接
        Connection connection = connectionFactory.createConnection();
        //开启连接
        connection.start();
        //使用连接对象创建一个session对象
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        //使用session创建一个Destination，Destination应该和消息的发送端一致
        Queue queue = session.createQueue("test-queue");
        //使用Session创建一个Consumer对象
        MessageConsumer consumer = session.createConsumer(queue);
        //向Consumer对象中设置的一个MessageListener对象，用来接收消息.new MessageListener()匿名内部类，实现类
        consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                //取出消息内容
                if( message instanceof TextMessage){
                    TextMessage textMessage= (TextMessage) message;
                    try {
                        String text = textMessage.getText();
                        //打印消息
                        System.out.println(text);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
                //
            }
        });
        //等待键盘输入，(不一定有消息)
        System.in.read();
        //关闭资源
        consumer.close();
        session.close();
        connection.close();

    }
}
