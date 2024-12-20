package com.packmind.confluent.rules.kafka.compression;

import org.apache.kafka.clients.producer.ProducerConfig;

import java.util.Properties;

public class Compression {

    public void configureCompressionOK1() {
        Properties props = new Properties();
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "gzip");
    }


    public void configureCompressionKO1() {
        Properties props = new Properties();
        props.put("compression.type", "none");
    }


    public void configureCompressionKO2() {
        Properties props = new Properties();
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none");
    }

}
