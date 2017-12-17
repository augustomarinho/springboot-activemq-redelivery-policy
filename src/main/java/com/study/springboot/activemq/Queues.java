package com.study.springboot.activemq;

public interface Queues {

    String QUEUE_REDELIVERY_EVERY_10_SECONDS = "QUEUE_10_SECONDS";
    String QUEUE_REDELIVERY_EVERY_MINUTE = "QUEUE_EVERY_MINUTE";

    String QUEUE_REDELIVERY_EVERY_10_SECONDS_ACK = "QUEUE_10_SECONDS_ACK";
}