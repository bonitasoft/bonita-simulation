/**
 * Copyright (C) 2010 BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2.0 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.bonitasoft.simulation.engine;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bonitasoft.simulation.model.calendar.SimCalendarDay;
import org.bonitasoft.simulation.model.calendar.SimCalendarInstance;
import org.bonitasoft.simulation.model.instance.ResourceInstance;
import org.bonitasoft.simulation.model.process.SimProcess;
import org.bonitasoft.simulation.model.resource.Resource;

/**
 * @author Romain Bioteau
 *
 */
public class DynamicRuntimeResource extends RuntimeResource {

	private ResourceInstance freeResource ;
	private Map<Integer, SimCalendarDay> planning;
	private SimProcess process;
	private long startTime;
	
	public DynamicRuntimeResource(Resource resource,SimProcess process, Map<Integer, SimCalendarDay> planning, long startTime, long timespan) {
		super(resource, new ArrayList<ResourceInstance>(), startTime, timespan);
		SimCalendarInstance calendar = new SimCalendarInstance(startTime, process, resource.getName(), planning) ;
		this.freeResource = new ResourceInstance(UUID.randomUUID().toString(), resource, calendar);
		this.planning  = planning ;
		this.process= process ;
		this.startTime = startTime ;
	}

	public List<ResourceInstance> createInstances(int nbInstance) {
		List<ResourceInstance> result = new ArrayList<ResourceInstance>();
		for(int i = 0 ; i< nbInstance ; i++){
			SimCalendarInstance calendar = new SimCalendarInstance(startTime, process, resource.getName(), planning) ;
			ResourceInstance ri = new ResourceInstance(UUID.randomUUID().toString(), resource, calendar);
			result.add(ri);
			instances.add(ri);
		}
		return result ; 
	}
	
	public boolean isResourceAvailable(long startDate,long executionTime,boolean contigous) throws Exception{
		return freeResource.getFirstAvailableDate(startDate, executionTime, contigous) == startDate ;
	}

}
