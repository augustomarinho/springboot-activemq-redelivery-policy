package com.study.springboot.activemq.config;

import com.study.springboot.activemq.Queues;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.broker.region.policy.RedeliveryPolicyMap;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.JmsTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import javax.jms.DeliveryMode;
import javax.jms.Session;

@Component
@EnableJms
public class ActiveMQConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String brokerUsername;

    @Value("${spring.activemq.password}")
    private String brokerPassword;

    @Bean
    public ActiveMQConnectionFactory connectionFactory() {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        connectionFactory.setBrokerURL(brokerUrl);
        connectionFactory.setPassword(brokerUsername);
        connectionFactory.setUserName(brokerPassword);
        connectionFactory.setTrustAllPackages(true);

        //Config Redelivery Policy in Redelivery Policy Map
        ActiveMQQueue queue10s = new ActiveMQQueue(Queues.QUEUE_REDELIVERY_EVERY_10_SECONDS);
        RedeliveryPolicy qp10Seconds = new RedeliveryPolicy();
        qp10Seconds.setInitialRedeliveryDelay(10000);
        qp10Seconds.setUseCollisionAvoidance(true);
        qp10Seconds.setRedeliveryDelay(10000);
        qp10Seconds.setUseExponentialBackOff(false);
        qp10Seconds.setMaximumRedeliveries(3);
        qp10Seconds.setDestination(queue10s);

        ActiveMQQueue queueEveryMinute = new ActiveMQQueue(Queues.QUEUE_REDELIVERY_EVERY_MINUTE);
        RedeliveryPolicy qpEveryMinute = new RedeliveryPolicy();
        qpEveryMinute.setInitialRedeliveryDelay(60000);
        qpEveryMinute.setRedeliveryDelay(60000);
        qpEveryMinute.setUseCollisionAvoidance(true);
        qpEveryMinute.setUseExponentialBackOff(false);
        qpEveryMinute.setMaximumRedeliveries(3);
        qpEveryMinute.setDestination(queueEveryMinute);

        ActiveMQQueue queue10sAck = new ActiveMQQueue(Queues.QUEUE_REDELIVERY_EVERY_10_SECONDS_ACK);
        RedeliveryPolicy qp10SecondsACk = new RedeliveryPolicy();
        qp10SecondsACk.setInitialRedeliveryDelay(10000);
        qp10SecondsACk.setUseCollisionAvoidance(true);
        qp10SecondsACk.setRedeliveryDelay(10000);
        qp10SecondsACk.setUseExponentialBackOff(false);
        qp10SecondsACk.setMaximumRedeliveries(3);
        qp10SecondsACk.setDestination(queue10sAck);


        ActiveMQQueue queue10sTransactional = new ActiveMQQueue(Queues.QUEUE_TRANSACTIONAL);
        RedeliveryPolicy qp10SecondsTransactional = new RedeliveryPolicy();
        qp10SecondsTransactional.setInitialRedeliveryDelay(10000);
        qp10SecondsTransactional.setUseCollisionAvoidance(true);
        qp10SecondsTransactional.setRedeliveryDelay(10000);
        qp10SecondsTransactional.setUseExponentialBackOff(false);
        qp10SecondsTransactional.setMaximumRedeliveries(3);
        qp10SecondsTransactional.setDestination(queue10sTransactional);

        RedeliveryPolicyMap rdMap = connectionFactory.getRedeliveryPolicyMap();
        rdMap.put(queue10s, qp10Seconds);
        rdMap.put(queueEveryMinute, qpEveryMinute);
        rdMap.put(queue10sAck, qp10SecondsTransactional);
        rdMap.put(queue10sTransactional, qp10SecondsTransactional);

        connectionFactory.setRedeliveryPolicyMap(rdMap);

        return connectionFactory;
    }

    @Bean
    public JmsTransactionManager jmsTransactionManager() {
        JmsTransactionManager jmsTransactionManager = new JmsTransactionManager();
        jmsTransactionManager.setConnectionFactory(connectionFactory());
        return jmsTransactionManager;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsTransactionalContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        factory.setErrorHandler(new ErrorHandler() {
            @Override
            public void handleError(Throwable throwable) {

            }
        });
        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        template.setDeliveryMode(DeliveryMode.PERSISTENT);
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setSessionTransacted(true);
        factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        return factory;
    }

    @Bean
    public JmsTemplate jmsAckTemplate() {
        JmsTemplate template = new JmsTemplate();
        template.setConnectionFactory(connectionFactory());
        template.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        template.setDeliveryMode(DeliveryMode.PERSISTENT);
        return template;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsACKListenerContainerFactory() {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setSessionTransacted(false);
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        return factory;
    }
}