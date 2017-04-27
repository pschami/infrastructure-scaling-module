package com.dell.cpsd.scale.repository;

import java.util.UUID;

import com.dell.cpsd.scale.model.Event;

/**
 * Copyright © 2016 Dell Inc. or its subsidiaries. All Rights Reserved. VCE
 * Confidential/Proprietary Information
 */
public interface EventRepository {
	void save(Event event);
	
	void delete(UUID eventId);	

	Event[] findAll();

	Event find(UUID eventId);
}
