package com.dell.cpsd.scale; /**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import com.dell.cpsd.hdp.capability.registry.client.binding.config.CapabilityRegistryBindingManagerConfig;
import com.dell.cpsd.hdp.capability.registry.client.lookup.config.CapabilityRegistryLookupManagerConfig;
import com.dell.cpsd.scale.api.ApplicationPerformanceEvent;
import com.dell.cpsd.scale.producer.ScalingModuleProducer;
import com.dell.cpsd.scale.services.ScalingModuleException;

/**
 * Spring boot application class
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@SpringBootApplication
@EnableAsync
@Import({CapabilityRegistryBindingManagerConfig.class, CapabilityRegistryLookupManagerConfig.class})

public class Application extends AsyncConfigurerSupport
{
    public static void main(String[] args) throws Exception
    {
      
    	ApplicationContext applicationContext = new SpringApplicationBuilder().sources(Application.class).bannerMode(Banner.Mode.LOG).run(args);
    	
    	final ScalingModuleProducer producer = applicationContext.getBean(ScalingModuleProducer.class);
    	    		
    	Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
			
			@Override
			public void run() {
				try {
					producer.publishApplicationPerformanceEvent(new ApplicationPerformanceEvent("test alert......"));
				} catch (ScalingModuleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}, 5, 5, TimeUnit.SECONDS);
    }

}

