/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.cpsd.scale.rest;

import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletProperties;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.ws.rs.ApplicationPath;

/**
 * REST API configuration.
 * <p>
 * <p>
 * Copyright &copy; 2016 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 */
@Component
@EnableSwagger2
@ApplicationPath("/api")
public class JerseyConfig extends ResourceConfig
{
    public JerseyConfig()
    {       
        register(Approve.class);
        property(ServletProperties.FILTER_FORWARD_ON_404, true);

        // Swagger resources:
        configureSwagger();
    }

    private void configureSwagger()
    {
        // Springfox UI isn't compatible with Jersey.
        // So we use swagger-jersey to generate JSON,
        // springfox swagger-ui to display it,
        // and application property springfox.documentation.swagger.v2.path to integrate them.
        // See details in: http://stackoverflow.com/a/42228055

        register(ApiListingResource.class);
        register(SwaggerSerializers.class);

        BeanConfig config = new BeanConfig();
        config.setConfigId("scaling-module");
        config.setTitle("Scaling Module");
        config.setVersion("v0.1");
        config.setSchemes(new String[]{"http"});
        config.setBasePath("/scale/api");
        config.setResourcePackage("com.dell.cpsd.scale.rest");
        config.setPrettyPrint(true);
        config.setScan(true);
    }
}
