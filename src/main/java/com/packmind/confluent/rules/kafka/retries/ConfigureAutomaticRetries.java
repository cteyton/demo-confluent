package com.packmind.confluent.rules.kafka.retries;

import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

public class ConfigureAutomaticRetries {

    public void configureAutomaticRetriesOK1() {
        Properties props = new Properties();
        props.put(ProducerConfig.RETRIES_CONFIG, Integer.MAX_VALUE);
    }


    public void configureAutomaticRetriesKO1() {
        Properties props = new Properties();
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
    }


    public void configureAutomaticRetriesKO2() {
        Properties props = new Properties();
        props.put(ProducerConfig.RETRIES_CONFIG, 10000);
    }

}
