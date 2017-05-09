/**
 * Copyright © 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 * VCE Confidential/Proprietary Information
 */

package com.dell.cpsd.scale.consumer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple Request Response Matcher, with a map of correlation IDs to event IDs
 * 
 *
 */
public class RequestResponseMatcher
{
    private static final Logger                                                       
    
    LOG = LoggerFactory.getLogger(RequestResponseMatcher.class);

    
    private final Map<String, UUID> map = new HashMap<>();
  
    /**
     * Add an event to the map
     * @param correlationId
     * @param eventId
     */
    public void put(final String correlationId, final UUID eventId)
    {
        LOG.debug("Saving eventId {} for and correlationId {}", eventId.toString(), correlationId);
        map.put(correlationId, eventId);
    }
    /**
     * Lookup based on correlationID and return an eventID from the map
     * @param correlationId
     * @return
     */

    public UUID received(final String correlationId)
    {
        LOG.debug("Received {} ", correlationId);
        final UUID eventId  = map.get(correlationId);
        if (eventId != null)
        {
            LOG.debug("Completing transaction for {}", correlationId);            
            map.remove(correlationId);
        }
        
        LOG.debug("Event Id: {}", eventId); 
        
        
        return eventId;
    }
}
