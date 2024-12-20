package com.packmind.confluent.rules.kafka.acks;

import org.apache.kafka.clients.producer.ProducerConfig;
import java.util.Properties;

public class ConfigureAcks {

    public void configureAckOK1() {
        Properties props = new Properties();
        props.put(ProducerConfig.ACKS_CONFIG, "all");
    }


    public void configureAckOK2() {
        Properties props = new Properties();
        props.put("acks", "all");
    }


    public void configureAckK01() {
        Properties props = new Properties();
        props.put(ProducerConfig.ACKS_CONFIG, "0");
    }


    public void configureAckK02() {
        Properties props = new Properties();
        props.put(ProducerConfig.ACKS_CONFIG, "1");
    }


    public void configureAckK03() {
        Properties props = new Properties();
        props.put("acks", "0");
    }


    public void configureAckK04() {
        Properties props = new Properties();
        props.put("acks", "1");
    }

}
