package com.example.tinyurl.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {
    private static ApplicationContext context;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        synchronized (SpringContext.class) {
            SpringContext.context = applicationContext;
        }
    }

    public static String getProperty(String node) {
        return context.getEnvironment().getProperty(node);
    }
}
