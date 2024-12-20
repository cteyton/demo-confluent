package com.packmind.confluent.rules.kafka.requestimeout;

import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

public class RequestTimeoutTooLow {


    public void requestTimeoutOK1() {
        Properties props = new Properties();
        props.put("request.timeout.ms", 30);
    }


    public void requestTimeoutOK2() {
        Properties props = new Properties();
        props.put("request.timeout.ms", 60);
    }


    public void requestTimeoutOK3() {
        Properties props = new Properties();
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 30);
    }


    public void requestTimeoutOK4() {
        Properties props = new Properties();
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 45);
    }



    public void requestTimeoutKO1() {
        Properties props = new Properties();
        props.put("request.timeout.ms", 1);
    }


    public void requestTimeoutKO2() {
        Properties props = new Properties();
        props.put("request.timeout.ms", 15);
    }


    public void requestTimeoutKO3() {
        Properties props = new Properties();
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 10);
    }


    public void requestTimeoutKO4() {
        Properties props = new Properties();
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 20);
    }
}
