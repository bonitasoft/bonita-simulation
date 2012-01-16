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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bonitasoft.simulation.model.Period;
import org.bonitasoft.simulation.model.instance.ResourceInstance;
import org.bonitasoft.simulation.model.resource.Resource;

/**
 * @author Romain Bioteau
 *
 */
public class RuntimeResource {

	protected Resource resource;
	protected List<ResourceInstance> instances ;
	protected SortedMap<Long,Integer> occupiedInstances ;
	protected SortedMap<Long,Integer> minimumWorkingInstance ;
	protected SortedMap<Long,Integer> maximumWorkingInstance ;
	protected SortedMap<Long,Integer> medianWorkingInstance ;
	protected SortedMap<Long,Float> averageWorkingInstance ;
	protected Map<String,SortedMap<Long,Long>> waitingFor ;
	protected Map<String,Long> intermediateWaitingFor ;
	protected HashMap<String, Long> processInstanceWaiting ;
	protected long procWaiting ;
	protected SortedMap<Long,Long> processWaitingFor ;
	
	public RuntimeResource(Resource resource,List<ResourceInstance> instances, long startTime, long timespan){
		this.resource = resource;
		this.instances =instances ;
		this.occupiedInstances = new TreeMap<Long, Integer>();
		this.minimumWorkingInstance = new TreeMap<Long, Integer>();
		this.maximumWorkingInstance = new TreeMap<Long, Integer>();
		this.averageWorkingInstance = new TreeMap<Long, Float>();
		this.intermediateWaitingFor = new HashMap<String, Long>();
		this.waitingFor = new HashMap<String, SortedMap<Long,Long>>() ;
		this.processInstanceWaiting = new HashMap<String, Long>() ;
		this.processWaitingFor = new TreeMap<Long, Long>() ;
	}

	public List<ResourceInstance> getInstances() {
		return Collections.unmodifiableList(instances);
	}


	public Resource getResource() {
		return resource;
	}

	/**
	 * @return the availableInstances
	 */
	public SortedMap<Long, Integer> getOccupiedInstances() {
		return occupiedInstances;
	}

	public void updateInstancesCount(long startDate,long endDate) throws Exception {
		
		int occupiedBefore = 0;
		int occupiedAfter = 0;
		
		for(ResourceInstance instance : instances){
			for(Period wp : instance.getPlanning().getWorkingPeriods()){
				if(wp.contains(startDate)){
					occupiedBefore++ ;
				}
				if(wp.contains(endDate)){
					occupiedAfter++ ;
				}
			}
		}
		if(getResource().getMaximumQuantity() >=0){
			if(occupiedBefore > getResource().getMaximumQuantity() || occupiedAfter > getResource().getMaximumQuantity() ){
				throw new Exception("Too Many Resources Ocuppied"); //$NON-NLS-1$
			}
		}
		occupiedInstances.put(startDate,occupiedBefore);
		occupiedInstances.put(endDate,occupiedAfter);

	}

	public void setMinimumWorkingInstance(SortedMap<Long,Integer> minimumWorkingInstance) {
		this.minimumWorkingInstance = minimumWorkingInstance;
	}

	public SortedMap<Long,Integer> getMinimumWorkingInstance() {
		return minimumWorkingInstance;
	}

	public void setMaximumWorkingInstance(SortedMap<Long,Integer> maximumWorkingInstance) {
		this.maximumWorkingInstance = maximumWorkingInstance;
	}

	public SortedMap<Long,Integer> getMaximumWorkingInstance() {
		return  Collections.unmodifiableSortedMap(maximumWorkingInstance);
	}

	public SortedMap<Long,Integer> getMedianWorkingInstance() {
		return  Collections.unmodifiableSortedMap(medianWorkingInstance);
	}

	public SortedMap<Long,Float> getAverageWorkingInstance() {
		return Collections.unmodifiableSortedMap(averageWorkingInstance);
	}

	public void updateInstancesCount(long interval) {
	
		List<Integer> workingResource = new ArrayList<Integer>(); 
		List<Long> keys = new ArrayList<Long>(); 
		for(Long key : occupiedInstances.keySet()){
			if(key <= SimulationEngine.currentTime){
				keys.add(key) ;
				workingResource.add(occupiedInstances.get(key)) ;
			}
		}
		if(workingResource.isEmpty()){
			minimumWorkingInstance.put(interval, 0);
			maximumWorkingInstance.put(interval, 0);
			averageWorkingInstance.put(interval, 0f);
		
		}else{
			minimumWorkingInstance.put(interval, Collections.min(workingResource));
			maximumWorkingInstance.put(interval, Collections.max(workingResource));
			
			int total = 0 ;
			for(Integer i : workingResource){
				total = total + i ;
			}
			float avg = (float) ((float)total/ (float)workingResource.size()) ;
			averageWorkingInstance.put(interval, avg);

		}

		for(Long key : keys){
			occupiedInstances.remove(key) ;
		}

	}

	public void updateWaitingFor(String activityName, long waitingTime) {
		if(intermediateWaitingFor.get(activityName) != null){
			intermediateWaitingFor.put(activityName, intermediateWaitingFor.get(activityName) + waitingTime) ;
		}else{
			intermediateWaitingFor.put(activityName, waitingTime) ;
		}
	}
	
	public void updateWaitingFor(long interval) {
		for(String activityName : intermediateWaitingFor.keySet()){
			SortedMap<Long, Long> time = null ;
			if(waitingFor.get(activityName)!= null){
				time = waitingFor.get(activityName) ;
				time.put(interval, intermediateWaitingFor.get(activityName));
				waitingFor.put(activityName, time) ;
			}else{
				time = new TreeMap<Long,Long>() ;
				time.put(interval, intermediateWaitingFor.get(activityName)) ;
				waitingFor.put(activityName, time) ;
			}
		}
		intermediateWaitingFor.clear() ;
	}
	
	public Map<String, SortedMap<Long,Long>> getWaitingFor(){
		return Collections.unmodifiableMap(waitingFor) ;
	}
	
	public SortedMap<Long,Long> getProcessWaitingFor(){
		return Collections.unmodifiableSortedMap(processWaitingFor) ;
	}
	
	public void updateProcessInstanceWaitingFor(String processInstanceUUID, long waitingTime) {
		if(processInstanceWaiting.get(processInstanceUUID) != null){
			processInstanceWaiting.put(processInstanceUUID, processInstanceWaiting.get(processInstanceUUID) + waitingTime) ;
		}else{
			processInstanceWaiting.put(processInstanceUUID, waitingTime) ;
		}
	}
	
	public void updateProcessInstanceWaitingFor(String processInstanceUUID) {
		if(processInstanceWaiting.get(processInstanceUUID) != null){
			procWaiting = procWaiting + processInstanceWaiting.get(processInstanceUUID) ;
		}
		processInstanceWaiting.remove(processInstanceUUID) ;
	}
	
	public void updateProcessInstanceWaitingFor(long interval) {
		processWaitingFor.put(interval,procWaiting) ;
		procWaiting = 0 ;
	}
	
}
