package com.packmind.confluent.rules.kafka.autoregister;

import java.util.Properties;

public class ConfigureAutoRegister {

    public void configureAutomaticRetriesOK1() {
        Properties props = new Properties();
        props.put("auto.register.schemas", false);
    }


    public void configureAutomaticRetriesKO1() {
        Properties props = new Properties();
        props.put("auto.register.schemas", true);
    }

}
