package org.bonitasoft.simulation.engine;

import org.bonitasoft.simulation.model.instance.ResourceInstance;

public class ResourceInstanceAvailability {
	private long time;
	private ResourceInstance resource;
	private long duration;

	public ResourceInstanceAvailability(long time, ResourceInstance resource,long duration) {
		super();
		this.time = time;
		this.resource = resource;
		this.duration = duration ;
	}
	public long getTime() {
		return time;
	}
	public ResourceInstance getResource() {
		return resource;
	}
	public long getDuration() {
		return duration;
	}
}