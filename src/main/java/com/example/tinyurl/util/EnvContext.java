package com.example.tinyurl.util;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class EnvContext implements EnvironmentAware {

    private static Environment environment;

    public static String getProperty(String node) {
        return EnvContext.environment.getProperty(node);
    }

    @Override
    public void setEnvironment(Environment environment) {
        synchronized (EnvContext.class) {
            EnvContext.environment = environment;
        }
    }
}
