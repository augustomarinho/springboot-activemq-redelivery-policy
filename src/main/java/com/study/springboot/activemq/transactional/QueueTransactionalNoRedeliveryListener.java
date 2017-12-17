package com.study.springboot.activemq.transactional;

import com.study.springboot.activemq.ActiveMQSender;
import com.study.springboot.activemq.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Component
public class QueueTransactionalNoRedeliveryListener implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMQSender.class);

    @JmsListener(destination = Queues.QUEUE_TRANSACTIONAL_NO_REDELIVERY,
            concurrency = "1-10",
            containerFactory = "jmsTransactionalContainerFactory")
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void onMessage(Message message) {
        try {
            String redeliveryCount = message.getStringProperty("JMSXDeliveryCount");
            String msg = ((TextMessage) message).getText();
            logger.info("Receiving message {} from queue {} [RedeliveryCount={}, MessageID={}]", msg, Queues.QUEUE_TRANSACTIONAL_NO_REDELIVERY, redeliveryCount, message.getJMSMessageID());

            if (message != null && msg.startsWith("error-")) {
                throw new Exception("Forcing reading jms message problems for message read in queue " + Queues.QUEUE_TRANSACTIONAL_NO_REDELIVERY);
            }
        } catch (Exception e) {
            logger.error("Problems for consuming messagem from queue {}.", Queues.QUEUE_TRANSACTIONAL_NO_REDELIVERY);
            throw new RuntimeException(e.getMessage());
        }
    }
}