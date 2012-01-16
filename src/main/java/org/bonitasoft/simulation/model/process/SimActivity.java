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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @author Romain Bioteau
 *
 */
public class SimActivity extends SimNamedElement {

	private Set<SimTransition> outgoingTransitions ;
	private Set<SimTransition> incomingTransitions ;
	private boolean exclusiveOutgoingTransition ;
	private List<SimData> data ;
	private List<ResourceAssignement> assignedResources ;
	private long executionTime ; //in ms
	private long estimatedTime ;//in ms
	private long maximumTime = Long.MAX_VALUE;//in ms
	private boolean contigous ;
	private boolean isStartElement ;
	private String parentProcessName ;
	private JoinType joinType = JoinType.XOR;
	private boolean inCycle = false;
	private boolean isEntryNode = false;
	private boolean isExitNode = false;
	
	public SimActivity(final String name,final String parentProcessName, final boolean exclusiveOutgoingTransition,final boolean isStartElement){
		super(name);
		this.isStartElement = isStartElement ;
		this.parentProcessName = parentProcessName;
		this.exclusiveOutgoingTransition = exclusiveOutgoingTransition;
		this.maximumTime = 0 ;
		assignedResources  = new ArrayList<ResourceAssignement>() ;
		outgoingTransitions = new HashSet<SimTransition>();
		incomingTransitions = new HashSet<SimTransition>();
		data = new ArrayList<SimData>();
	}

	public SimActivity(final String name,final String parentProcessName,final boolean isStartElement, final long executionTime, final long estimatedTime, final long maximumTime,
			final boolean exclusiveOutgoingTransition, final boolean contigous){
		super(name);
		this.isStartElement = isStartElement ; 
		this.contigous = contigous;
		this.parentProcessName = parentProcessName;
		this.estimatedTime = estimatedTime;
		this.exclusiveOutgoingTransition = exclusiveOutgoingTransition;
		this.executionTime = executionTime;
		this.maximumTime = maximumTime;
		assignedResources  = new ArrayList<ResourceAssignement>() ;
		outgoingTransitions = new HashSet<SimTransition>();
		incomingTransitions = new HashSet<SimTransition>();
		data = new ArrayList<SimData>();
	}
	
	public SimActivity(final String name,final String parentProcessName, final long executionTime, final long estimatedTime, final long maximumTime,
			final boolean exclusiveOutgoingTransition, final boolean contigous){
		this(name,parentProcessName,false,executionTime,estimatedTime,maximumTime,exclusiveOutgoingTransition,contigous) ;

	}
	
	public SimActivity(final String name,final JoinType joinType,final String parentProcessName, final long executionTime, final long estimatedTime, final long maximumTime,
			final boolean exclusiveOutgoingTransition, final boolean contigous){
		this(name,parentProcessName,false,executionTime,estimatedTime,maximumTime,exclusiveOutgoingTransition,contigous) ;
		this.joinType = joinType ;
	}
	
	public SimActivity(final String name,final JoinType joinType,final String parentProcessName, final boolean exclusiveOutgoingTransition){
		this(name,parentProcessName,false,0,0,0,exclusiveOutgoingTransition,false) ;
		this.joinType = joinType ;
	}

	public List<SimData> getData() {
		return Collections.unmodifiableList(data);
	}
	
	public void addData(SimData simData) {
		data.add(simData);
	}

	public boolean hasResources() {
		return assignedResources != null && !assignedResources.isEmpty();
	}

	public List<ResourceAssignement> getAssignedResources() {
		return Collections.unmodifiableList(assignedResources);
	}

	public void addResourceAssignement(ResourceAssignement ra){
		assignedResources.add(ra);
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public long getEstimatedTime() {
		return estimatedTime;
	}

	public long getMaximumTime() {
		return maximumTime;
	}

	public boolean isContigous() {
		return contigous;
	}

	public Set<SimTransition> getOutgoingTransitions() {
		return Collections.unmodifiableSet(outgoingTransitions);
	}
	
	public Set<SimTransition> getIncomingTransitions() {
		return Collections.unmodifiableSet(incomingTransitions);
	}

	public void addOutgoingTransition(SimTransition transition) {
		transition.setSource(this) ;
		outgoingTransitions.add(transition);
	}

	public boolean isExclusiveOutgoingTransition() {
		return exclusiveOutgoingTransition;
	}

	public String getParentProcessName() {
		return parentProcessName;
	}
	
	public boolean isStartElement() {
		return isStartElement;
	}

	public JoinType getJoinType() {
		return joinType;
	}


	public void addIncomingTransition(SimTransition incomingTransition) {
		this.incomingTransitions.add(incomingTransition);
	}

	public void setInCycle(boolean inCycle) {
		this.inCycle = inCycle;
	}

	public boolean isInCycle() {
		return inCycle;
	}

	public void setEntryNode(boolean isEntryNode) {
		this.isEntryNode = isEntryNode;
	}

	public boolean isEntryNode() {
		return isEntryNode;
	}

	public void setExitNode(boolean isExitNode) {
		this.isExitNode = isExitNode;
	}

	public boolean isExitNode() {
		return isExitNode;
	}


}
