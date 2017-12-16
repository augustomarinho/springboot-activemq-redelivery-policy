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
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

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
        qp10Seconds.setInitialRedeliveryDelay(1000);
        qp10Seconds.setUseCollisionAvoidance(true);
        qp10Seconds.setRedeliveryDelay(10000);
        qp10Seconds.setUseExponentialBackOff(false);
        qp10Seconds.setMaximumRedeliveries(3);
        qp10Seconds.setDestination(queue10s);

        ActiveMQQueue queueEveryMinute = new ActiveMQQueue(Queues.QUEUE_REDELIVERY_EVERY_MINUTE);
        RedeliveryPolicy qpEverySeconds = new RedeliveryPolicy();
        qpEverySeconds.setInitialRedeliveryDelay(0);
        qpEverySeconds.setRedeliveryDelay(60000);
        qpEverySeconds.setUseExponentialBackOff(false);
        qpEverySeconds.setMaximumRedeliveries(3);
        qpEverySeconds.setUseCollisionAvoidance(true);
        qpEverySeconds.setDestination(queueEveryMinute);

        RedeliveryPolicyMap rdMap = connectionFactory.getRedeliveryPolicyMap();
        rdMap.put(queue10s, qp10Seconds);
        rdMap.put(queueEveryMinute, qp10Seconds);

        connectionFactory.setRedeliveryPolicyMap(rdMap);

        return connectionFactory;
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
}