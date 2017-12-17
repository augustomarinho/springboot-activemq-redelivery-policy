package com.study.springboot.activemq.transactedsession;

import com.study.springboot.activemq.ActiveMQSender;
import com.study.springboot.activemq.Queues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

@Component
public class QueueEveryMinuteListener {

    private static final Logger logger = LoggerFactory.getLogger(ActiveMQSender.class);

    @JmsListener(destination = Queues.QUEUE_REDELIVERY_EVERY_MINUTE,
            concurrency = "1-50", subscription = "teste1",
            id = "padrao1")
    public void onMessage(Message message,
                          Session session,
                          @Header(name = "JMSXDeliveryCount", defaultValue = "1") String redeliveryCount,
                          @Header(name = JmsHeaders.MESSAGE_ID, defaultValue = "1") String messageId) throws JMSException {
        try {
            String msg = ((TextMessage) message).getText();
            logger.info("Receiving message {} from queue {} [RedeliveryCount={}, MessageID={}]", msg, Queues.QUEUE_REDELIVERY_EVERY_MINUTE, redeliveryCount, messageId);

            if (msg != null && msg.startsWith("error-")) {
                throw new Exception("Forcing reading jms message problemas for message read in queue " + Queues.QUEUE_REDELIVERY_EVERY_MINUTE);
            }

            session.commit();
        } catch (Exception e) {
            logger.error("Problems for consuming messagem from queue {}.", Queues.QUEUE_REDELIVERY_EVERY_MINUTE);
            session.rollback();
        }
    }
}