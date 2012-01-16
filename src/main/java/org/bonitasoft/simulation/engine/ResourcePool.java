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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bonitasoft.simulation.model.Period;
import org.bonitasoft.simulation.model.calendar.SimCalendarInstance;
import org.bonitasoft.simulation.model.instance.ResourceInstance;
import org.bonitasoft.simulation.model.instance.SimActivityInstance;
import org.bonitasoft.simulation.model.process.ResourceAssignement;
import org.bonitasoft.simulation.model.process.SimProcess;
import org.bonitasoft.simulation.model.resource.Resource;


/**
 * @author Romain Bioteau
 *
 */
public class ResourcePool {

	private static ResourcePool INSTANCE;
	private Map<String, RuntimeResource> resourceInstances ;

	private ResourcePool(){
		resourceInstances = new HashMap<String, RuntimeResource>();
	}

	public static ResourcePool getInstance(){
		if(INSTANCE == null){
			INSTANCE = new ResourcePool();
		}

		return INSTANCE ;
	}

	public void addResource(final SimProcess process, final Resource resource, final long startTime, long timespan){
		final List<ResourceInstance> instances = new ArrayList<ResourceInstance>() ;

		if(resource.getMaximumQuantity() > 0) {//DEFINED RESOURCE NUMBER MODE
			for(int i = 0 ; i< resource.getMaximumQuantity() ; i++){
				final SimCalendarInstance planning = new SimCalendarInstance(startTime,process,resource.getName(),resource.getPlanning().getDaysOfWeek()) ;
				instances.add(new ResourceInstance(UUID.randomUUID().toString(),resource,planning));
			}
			resourceInstances.put(resource.getName(), new RuntimeResource(resource,instances,startTime,timespan));		
		}else{//UNLIMITED RESOURCE MODE
			resourceInstances.put(resource.getName(), new DynamicRuntimeResource(resource,process,resource.getPlanning().getDaysOfWeek(),startTime,timespan));	
		}
		
		

	}



	public void createResources(final SimProcess process , final List<Resource> inputResources, final long startTime,final long timespan) {
		for(Resource r : inputResources){
			addResource(process ,r, startTime,timespan);
			DefinitionPool.getInstance().addResourceDefinition(r);
		}
	}

	public List<ResourceInstanceAvailability> findAvailableResource(int quantity, Resource r, long startDate, long executionTime, boolean contigous) throws Exception {

		List<ResourceInstance> riList = getResourceInstances(r) ;
		
		if(riList.size() < quantity){
			if(r.getMaximumQuantity() >= 0){
				throw new Exception("Too many resources need regarding the resource pool");
			}else{//Dynamic Pool of Resource
				int needed = quantity - riList.size() ;
				DynamicRuntimeResource dr = (DynamicRuntimeResource) getResourceInstances().get(r.getName()) ;
				dr.createInstances(needed) ;
				riList = getResourceInstances(r) ;
			}
		}
		
		final List<ResourceInstanceAvailability> instances = new ArrayList<ResourceInstanceAvailability>();
		boolean allInstancesAvailable = false ;

		while(!SimulationEngine.isStopped && !allInstancesAvailable){
			instances.clear() ;
			while(!SimulationEngine.isStopped  && instances.size() != quantity){

				long lastAvailableStart = Long.MAX_VALUE;
				ResourceInstance lastResource = null;
				List<ResourceInstance> searchList = new ArrayList<ResourceInstance>(riList) ;

				for(ResourceInstanceAvailability ria : instances){
					searchList.remove(ria.getResource()) ;
				}

				for(ResourceInstance instance : searchList){
					long availableStart = startDate ;
					availableStart = instance.getFirstAvailableDate(availableStart,executionTime,contigous);
					if (lastResource == null || lastAvailableStart > availableStart) {
						lastResource = instance;
						lastAvailableStart = availableStart;
					}
					if (availableStart == startDate) {//SHORTEN THE LOOP, NO NEED TO GET ALL AVAILABLE RESOURCE INSTANCE
						break ;
					}
				}
				
				if(lastAvailableStart != startDate && r.getMaximumQuantity() < 0){//DYNAMIC RESOURCE
					DynamicRuntimeResource dr = (DynamicRuntimeResource) getResourceInstances().get(r.getName()) ;
					if(dr.isResourceAvailable(startDate, executionTime, contigous)){//THE RESOURCE IS AT WORK
						List<ResourceInstance> createdInstances = dr.createInstances(1) ;
						lastResource = createdInstances.get(0) ;
						lastAvailableStart = startDate ;
					}
				}

				if(lastResource == null){
					throw new Exception("Impossible to find an available Date for "+r.getName()) ; //$NON-NLS-1$
				}

				instances.add(new ResourceInstanceAvailability(lastAvailableStart, lastResource,executionTime));
			}

			long lastStart = instances.get(0).getTime() ;
			allInstancesAvailable = true ;
			long newStartDate = 0 ;
			for(ResourceInstanceAvailability ria : instances){
				if( lastStart != ria.getTime()){
					allInstancesAvailable = false ;
					if(newStartDate < ria.getTime()){
						newStartDate = ria.getTime() ; 
					}
				}

			}

			if(!allInstancesAvailable){
				startDate = newStartDate ;
			}

		}
		return instances ;
	}

	public List<ResourceInstance> getResourceInstances(Resource r) {
		if (resourceInstances.get(r.getName()) == null){
			return new ArrayList<ResourceInstance>();
		}
		return resourceInstances.get(r.getName()).getInstances();
	}

	public void lockAvailableResourceInstances(final SimActivityInstance activity, final List<ResourceAssignement> resources, final boolean isContigous, final Set<ResourceInstanceAvailability> instances) throws Exception {
		if (resources == null || resources.isEmpty()) {
			return;
		}
		for (ResourceInstanceAvailability resourceInstanceAvailability : instances){
			final ResourceInstance resourceInstance = resourceInstanceAvailability.getResource();
			final String resourceName = resourceInstance.getDefinition().getName();
			long executionTime = 0;
			for (ResourceAssignement resourceAssignment : resources){
				if (resourceAssignment.getResource().getName().equals(resourceName)) {
					executionTime = resourceAssignment.getDuration();
					break;
				}
			}
			if(executionTime > 0){
				lockResourceIntance(resourceInstance, resourceInstanceAvailability.getTime(), executionTime, activity);
			}
		}
	}

	private void lockResourceIntance(final ResourceInstance resourceInstance, final long startDate, final long executionTime, final SimActivityInstance activity) throws Exception {
		final Period period = new Period(startDate, startDate + executionTime);
		final List<Period> periods = resourceInstance.getPlanning().split(period);
		if(periods.isEmpty()){
			throw new Exception("period can't be empty"); //$NON-NLS-1$
		}
		RuntimeResource runtimeResource = getResourceInstances().get(resourceInstance.getDefinition().getName()) ;
		for (Period p : periods){
			resourceInstance.getPlanning().addBusyPeriod(p,true) ;
			runtimeResource.updateInstancesCount(p.getBegin()+1,resourceInstance.getPlanning().getNextPlanningAvailable(p.getEnd()+1));
		}
		activity.addAssignedResourceInstance(resourceInstance) ;
	}

	public Set<ResourceInstanceAvailability> getNextAvailableDateForAllResources(String processUUID, String activityName, long startDate, List<ResourceAssignement> resources, boolean isContigous) throws Exception {
		boolean allResourceAvailable = false ;
		long currentStartDate = startDate;
		final Set<ResourceInstanceAvailability> instances = new HashSet<ResourceInstanceAvailability>();
		int numberOfInstances = 0 ;
		for (ResourceAssignement resourceAssignment : resources){
			numberOfInstances = numberOfInstances + resourceAssignment.getQuantity() ;
		}
		do { 
			for (ResourceAssignement resourceAssignment : resources){

				final List<ResourceInstanceAvailability> firstAvailability = findAvailableResource(resourceAssignment.getQuantity(),resourceAssignment.getResource(), currentStartDate, resourceAssignment.getDuration(), isContigous);
				if (firstAvailability.get(0).getTime() > currentStartDate) {//Resource not available  for currentStartDate

					storeWaitingForResource(resourceAssignment.getResource(),activityName,processUUID,firstAvailability.get(0).getTime()-currentStartDate) ;


					currentStartDate = firstAvailability.get(0).getTime();
					instances.clear();
					allResourceAvailable = false;
					break;
				}
				instances.addAll(firstAvailability);

			}

			if (instances.size() == numberOfInstances){
				allResourceAvailable = true ;
			}
		} while (!SimulationEngine.isStopped &&  !allResourceAvailable);
		return instances;
	}

	private void storeWaitingForResource(Resource resource,String activityName, String processUUID, long waitingTime) {
		RuntimeResource runtimeResource = getResourceInstances().get(resource.getName()) ;
		runtimeResource.updateWaitingFor(activityName,waitingTime) ;
		runtimeResource.updateProcessInstanceWaitingFor(processUUID,waitingTime) ;
	}

	public Map<String,RuntimeResource> getResourceInstances() {
		return resourceInstances;
	}

	public void updateResourceConsumption(long interval) {
		for(RuntimeResource resource : getResourceInstances().values()){
			resource.updateInstancesCount(interval) ;
			resource.updateWaitingFor(interval); 
			resource.updateProcessInstanceWaitingFor(interval);
		}
	}

}
