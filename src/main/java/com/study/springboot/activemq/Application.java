package com.study.springboot.activemq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Application {

    @Autowired
    private ActiveMQSender mqSender;

    public static void main(String args[]) {
        SpringApplication.run(Application.class, args);
    }

    @RequestMapping(value = "/send", method = RequestMethod.POST)
    public String query(@RequestParam("queue") String queue,
                        @RequestParam("message") String message,
                        @RequestParam("size") int size) {
        try {
            if (size <= 0) {
                mqSender.sendMessage(queue, message);
            } else {
                for (int i = 0; i < size; i++) {
                    mqSender.sendMessage(queue, message + "-" + i);
                    Thread.sleep(10);
                }
            }
            return "OK";
        } catch (Exception e) {
            return "NOK";
        }
    }
}