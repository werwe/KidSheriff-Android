package com.starmark.sheriff;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class MyResourceConfig extends ResourceConfig {

    public MyResourceConfig() {
        packages("com.starmark.sheriff");
        register(JacksonFeature.class);
    }
}