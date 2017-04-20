package com.dell.cpsd.scale;

import com.dell.cpsd.scale.capabilityintegration.CapabilityRegistryIntegration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Application Runner class.
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 * </p>
 *
 * @version 1.0
 * @since 1.0
 */
@Component
public class AppRunner implements CommandLineRunner
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AppRunner.class);

    @Autowired
    public CapabilityRegistryIntegration capabilityRegistryIntegration;

    @Override
    public void run(String... args) throws Exception
    {
        capabilityRegistryIntegration.start();


    }
}
