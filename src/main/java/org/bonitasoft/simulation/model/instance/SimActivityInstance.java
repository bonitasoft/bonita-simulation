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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bonitasoft.simulation.model.process.SimActivity;


/**
 * @author Romain Bioteau
 *
 */
public class SimActivityInstance extends SimNamedElementInstance {

	private List<SimTransitionInstance> outgoingTransitionsInstances ;
	private Set<ResourceInstance> assignedResourceInstances ;
	private SimProcessInstance processInstance ;
	private long startDate = 0;
	private long executionDate = 0;
	private long finishDate = 0;
	private int skipped ;
	private Set<SimActivityInstance> next ;
	private int incomings = 0 ;
	private int iterationId = 0;

	public SimActivityInstance(final SimActivity definition, final String instanceUUID, final SimProcessInstance processInstance){
		super(definition, instanceUUID);
		this.processInstance = processInstance;
		this.outgoingTransitionsInstances = new ArrayList<SimTransitionInstance>();
		this.next = new HashSet<SimActivityInstance>();
		this.assignedResourceInstances = new HashSet<ResourceInstance>();
	}

	public List<SimTransitionInstance> getOutgoingTransitionsInstances() {
		return Collections.unmodifiableList(outgoingTransitionsInstances);
	}

	public SimProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void addTransitionInstance(SimTransitionInstance tInstance) {
		outgoingTransitionsInstances.add(tInstance) ;
	}

	public void skip() {
		skipped ++;
	}

	public int getSkip(){
		return skipped;
	}

	public void setStartDate(long startDate) {
		this.startDate = startDate;
	}

	public long getStartDate() {
		return startDate;
	}

	public void setFinishDate(long finnishDate) {
		this.finishDate = finnishDate;
	}

	public long getFinishDate() {
		return finishDate;
	}


	public void addNext(SimActivityInstance elem) {
		next.add(elem);
	}

	public Set<SimActivityInstance> getNext() {
		return Collections.unmodifiableSet(next);
	}

	public Set<ResourceInstance> getAssignedResourceInstances() {
		return Collections.unmodifiableSet(assignedResourceInstances);
	}

	public void addAssignedResourceInstance(ResourceInstance resourceInstance) {
		assignedResourceInstances.add(resourceInstance);
	}

	public void setExecutionDate(long executionDate) {
		this.executionDate = executionDate;
	}

	public long getExecutionDate() {
		return executionDate;
	}

	public boolean hasNext() {
		return !next.isEmpty();
	}

	@Override
	public String toString() {
		StringBuilder activityStringBuilder = new StringBuilder();
		activityStringBuilder.append(getDefinition().getName()) ;
		activityStringBuilder.append("(");
		activityStringBuilder.append(getInstanceUUID());
		activityStringBuilder.append(",");
		activityStringBuilder.append(getStartDate());
		activityStringBuilder.append(",");
		activityStringBuilder.append(getExecutionDate());
		activityStringBuilder.append(",");
		activityStringBuilder.append(getFinishDate());
		activityStringBuilder.append(",");
		activityStringBuilder.append(skipped);
		activityStringBuilder.append(")");
		activityStringBuilder.append(":");
		
		for(SimActivityInstance a : getNext()){
			activityStringBuilder.append(a.getInstanceUUID()).append(",");//$NON-NLS-1$
		}
		String nextActivities = activityStringBuilder.toString();
		if(hasNext()){
			nextActivities = nextActivities.substring(0,nextActivities.length()-1);
		}
		return  nextActivities ;
	}

	public void setSkip(int n) {
		this.skipped = n ;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SimActivityInstance){
			return getInstanceUUID().equals(((SimActivityInstance) obj).getInstanceUUID());
		}else if(obj instanceof RuntimeTask){
			return getInstanceUUID().equals(((RuntimeTask) obj).getTask().getInstanceUUID());
		}
		return super.equals(obj);
	}

	public void addIncoming() {
		this.incomings++;
	}

	public int getIncomings() {
		return incomings;
	}
	

	public void setIterationId(int iterationId) {
		this.iterationId  = iterationId;
	}

	public int getIterationId() {
		return iterationId;
	}
}
