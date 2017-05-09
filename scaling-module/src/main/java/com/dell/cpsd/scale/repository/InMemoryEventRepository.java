/**
 * Copyright © 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 */

package com.dell.cpsd.scale.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.dell.cpsd.scale.model.Event;

/**
 * Copyright © 2017 Dell Inc. or its subsidiaries. All Rights Reserved.
 *
 * Simple in memory implementation of an EventRepository
 * Stores events in a map
  */
@Repository
public class InMemoryEventRepository implements EventRepository
{
    private final Map<UUID, Event> events = new HashMap<>();

    @Override public void save(final Event event)
    {
        events.put(event.getId(), event);
    }

    @Override public Event[] findAll()
    {
        Event[] results = new Event[events.size()];
        return events.values().toArray(results);
    }

    @Override public Event find(final UUID eventId)
    {
        return events.get(eventId);
    }

	@Override
	public void delete(UUID eventId) {
		events.remove(eventId);
		
	}
}
