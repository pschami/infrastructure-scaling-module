/**
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries.  All Rights Reserved.
 */
package com.dell.cpsd.scale.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import com.dell.cpsd.scale.services.ScalingModuleService;

import io.swagger.annotations.Api;

/**
 * Simple Rest APi
 * 
 * <hostname>/scale/api/approve/<eventId> 
 * <p>
 * <p>
 * Copyright &copy; 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * </p>
 */
@Api
@Component
@RestController
@Path("/approve")
@Produces(MediaType.APPLICATION_JSON)
public class Approve
{
    private static final Logger LOG = LoggerFactory.getLogger(Approve.class);
    
   
    private final ScalingModuleService scalingModuleService;

    /**
     * Constructor
     * @param scalingModuleService
     */
    @Autowired
    public Approve(final ScalingModuleService scalingModuleService)
    {
    	super();
    	this.scalingModuleService = scalingModuleService;
             
    }
    
    /**
     * Handles http get <hostname>/scale/api/approve/<eventId>
     * and triggers approval handler in scaling module service.
     * returns 200k with approved text
     * @param scalingModuleService
     */

    @GET
    @Path("{eventId}")
    public Response approveEvent(@PathParam("eventId") String eventId)
    {
       	
    	LOG.debug("Approvde Event with Id {}", eventId);    	
    	scalingModuleService.processApproval(eventId);    	
        return Response.ok("approved").build();
    }
}