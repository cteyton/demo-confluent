package com.packmind.confluent.rules.kafka.zookeeper;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.server.config.ZkConfigs;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class UseKRraftInsteadofZookeeper {

    public void connectToZookeeper() {
        Properties props = new Properties();
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put("zookeeper.connect", "zookeeperConnect");
    }


    public void connectToZookeeper2() {
        Properties runningConfig = new Properties();
        runningConfig.setProperty(ZkConfigs.ZK_CONNECT_CONFIG, "zookeeperConnection");
    }


    public void connectToZookeeper3() {
        Properties runningConfig = new Properties();
        runningConfig.put(ZkConfigs.ZK_CONNECT_CONFIG, "zookeeperConnection");
    }


    public void connectToZookeeper4() {
        Map<String, Object> config = new HashMap<>();
        config.put(ZkConfigs.ZK_CONNECT_CONFIG, "zookeeperConnection");
    }


    public void connectToZookeeper5() {
        Map<String, Object> config = new HashMap<>();
        config.put("zookeeper.connect", "anyzookeeperConnect");
    }

}