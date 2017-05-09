/**
 * Copyright © 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 */

package com.dell.cpsd.scale.repository;

import java.util.UUID;

import com.dell.cpsd.scale.model.Event;

/**
 * Repository used to store events
 * Copyright © 2017 Dell Inc. or its subsidiaries. All Rights Reserved. 
 */
public interface EventRepository {
	/**
	 * Save an event
	 * @param event
	 */
	void save(Event event);
	/**
	 * Delete an event
	 * @param event
	 */
	void delete(UUID eventId);	

	/**
	 * Find all events
	 * @param event
	 */
	Event[] findAll();

	/**
	 * Find an event based on eventId
	 * @param eventId
	 * @return
	 */
	Event find(UUID eventId);
}
