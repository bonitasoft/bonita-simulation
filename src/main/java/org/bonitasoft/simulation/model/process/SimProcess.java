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
 
package org.bonitasoft.simulation.model.process;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.bonitasoft.simulation.iteration.IterationDescriptor;


/**
 * @author Romain Bioteau
 *
 */
public class SimProcess extends SimNamedElement {

	private Set<SimActivity> startElements ;
	private Set<SimData> data ;
	private Set<IterationDescriptor> cycles ;
	private long maximumTime ;

	public SimProcess(String name){
		super(name);
		startElements = new HashSet<SimActivity>();
		data = new HashSet<SimData>();
		cycles = new HashSet<IterationDescriptor>();
	}

	public SimProcess(String name,long maxTime){
		this(name);
		setMaximumTime(maxTime) ;
	}
	
	public Set<SimData> getData() {
		return Collections.unmodifiableSet(data);
	}
	
	public void addData(SimData simData) {
		data.add(simData);
	}
	

	public Set<SimActivity> getStartElements() {
		return Collections.unmodifiableSet(startElements);
	}
	
	public void addStartElement(final SimActivity startElem) {
		startElements.add(startElem);
	}

	public Set<SimActivity> getActivities() {
		Set<SimActivity> result = new HashSet<SimActivity>();
		parseProcess(getStartElements(),result) ;
		return result;
	}

	private void parseProcess(Set<SimActivity> elems,Set<SimActivity> result) {
		for(SimActivity activity :elems){
			result.add(activity) ;
			if(!activity.getOutgoingTransitions().isEmpty()){
				for(SimTransition t : activity.getOutgoingTransitions()){
					if(!result.contains(t.getTarget())){
						parseProcess(Collections.singleton(t.getTarget()), result);
					}
				}
			}
		}
	}

	public Set<IterationDescriptor> getCycles() {
		return Collections.unmodifiableSet(cycles);
	}
	
	public void addCycle(IterationDescriptor descriptor) {
		cycles.add(descriptor) ;
	}

	public SimActivity getActivity(String activityName) {
		for(SimActivity activity : getActivities()){
			if(activity.getName().equals(activityName)){
				return activity ; 
			}
		}
		return null;
	}

	public long getMaximumTime() {
		return maximumTime;
	}
	
	public void setMaximumTime(long maxTime){
		this.maximumTime = maxTime;
	}

}
