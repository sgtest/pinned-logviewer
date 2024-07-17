package com.so.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

public class PropertyPlaceholder extends PropertyPlaceholderConfigurer {

    public static Map<String,String> dataSourceMap;

    @Override
    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        super.processProperties(beanFactoryToProcess, props);
        dataSourceMap = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            String value = props.getProperty(keyStr);
            dataSourceMap.put(keyStr, value);
        }
    }

    
    //static method for accessing context properties
/*    public static Object getProperty(String name) {
        return propertyMap.get(name);
    }*/
}