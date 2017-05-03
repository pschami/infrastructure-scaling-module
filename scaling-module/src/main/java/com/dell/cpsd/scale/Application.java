package com.dell.cpsd.scale; /**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;

import com.dell.cpsd.hdp.capability.registry.client.binding.config.CapabilityRegistryBindingManagerConfig;
import com.dell.cpsd.hdp.capability.registry.client.lookup.config.CapabilityRegistryLookupManagerConfig;

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

public class Application extends SpringBootServletInitializer
{
    public static void main(String[] args) throws Exception
    {
      
    	new SpringApplicationBuilder().sources(Application.class).bannerMode(Banner.Mode.LOG).run(args);
    	
    
    }

}

