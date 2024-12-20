package com.packmind.confluent.rules.kafka.complextype;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.server.config.ZkConfigs;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class AvoidComplexTypeInMessaging {

    public void complexType1() {
        Properties props = new Properties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaAvroSerializer.class);
    }


    public void complexType2() {
        Properties props = new Properties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaJsonSchemaSerializer.class);
    }


    public void complexType3() {
        Properties props = new Properties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, io.confluent.kafka.serializers.KafkaProtobufSerializer.class);
    }


}