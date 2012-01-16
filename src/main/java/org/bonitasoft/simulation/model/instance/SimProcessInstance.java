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

package org.bonitasoft.simulation.model.instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bonitasoft.simulation.model.process.SimProcess;


/**
 * @author Romain Bioteau
 *
 */
public class SimProcessInstance extends SimNamedElementInstance implements Comparable<SimProcessInstance>{

	private Set<SimActivityInstance> startElemInstances ;
	private List<SimDataInstance> dataInstances ;
	private long endDate ;
	private long startDate ;

	public SimProcessInstance(final SimProcess definition, final String uuid , final long startDate){
		super(definition, uuid);
		dataInstances = new ArrayList<SimDataInstance>();
		startElemInstances = new HashSet<SimActivityInstance>();
		this.startDate = startDate ;
	}

	public List<SimDataInstance> getDataInstance() {
		return Collections.unmodifiableList(dataInstances);
	}

	public Set<SimActivityInstance> getStartElemInstances() {
		return Collections.unmodifiableSet(startElemInstances);
	}

	public void addStartElement(final SimActivityInstance activity) {
		startElemInstances.add(activity);
	}

	public void addDataInstance(final SimDataInstance dataInstance) {
		dataInstances.add(dataInstance);
	}

	public void setEndDate(long endDate) {
		this.endDate = endDate;
	}

	public long getEndDate() {
		return endDate;
	}

	public long getStartDate() {
		return startDate;
	}

	public int compareTo(SimProcessInstance instance) {
		return new Date(getEndDate()).compareTo(new Date(instance.getEndDate()));
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SimProcessInstance){
			return getInstanceUUID().equals(((SimProcessInstance) obj).getInstanceUUID()) ;
		}
		return super.equals(obj);
	}

}
