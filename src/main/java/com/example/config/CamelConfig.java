package com.example.config;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.camel.CamelContext;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CamelConfig {

	@Bean
	public CamelContextConfiguration contextConfiguration() { 
         return new CamelContextConfiguration() {
			
			@Override
			public void beforeApplicationStart(CamelContext camelContext) {
				camelContext.addComponent("activemq", new ActiveMQComponent());
			}
			
			@Override
			public void afterApplicationStart(CamelContext camelContext) {
			}
		};
    } 
}
