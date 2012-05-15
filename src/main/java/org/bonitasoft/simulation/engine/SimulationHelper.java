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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bonitasoft.simulation.model.instance.SimActivityInstance;
import org.bonitasoft.simulation.model.instance.SimDataInstance;
import org.bonitasoft.simulation.model.instance.SimProcessInstance;
import org.bonitasoft.simulation.model.process.SimActivity;
import org.bonitasoft.simulation.model.process.SimData;
import org.bonitasoft.simulation.model.process.SimNamedElement;
import org.bonitasoft.simulation.model.process.SimProcess;
import org.bonitasoft.simulation.model.process.SimTransition;

/**
 * @author Romain Bioteau
 *
 */
public class SimulationHelper {


	public static SimNamedElement getSimulationElementByName(final String name, final List<SimNamedElement> simElems) {
		for (SimNamedElement e: simElems){
			if(e.getName().equals(name)){
				return e ;
			}
		}
		return null;
	}

	public static List<SimNamedElement> findSimNamedElement(final SimProcess process, Class<? extends SimNamedElement> type){
		List<SimNamedElement> result = new ArrayList<SimNamedElement>();

		Set<SimActivity> startElements = process.getStartElements();
		for (SimActivity simActivity : startElements) {
			findSimNamedElement(simActivity, type, result);
		}
		return result;
	}


	/**
	 * @param simActivity
	 * @param type
	 * @param result
	 */
	private static void findSimNamedElement(SimActivity simActivity, Class<? extends SimNamedElement> type, List<SimNamedElement> result) {
		if(type.isInstance(simActivity) && !result.contains(simActivity)){
			result.add(simActivity);
		}
		for (SimTransition transition : simActivity.getOutgoingTransitions()) {
			if(type.isInstance(transition) && !result.contains(transition)){
				result.add(transition);
			}
			if(transition.getTarget() != null){
				findSimNamedElement(transition.getTarget(),type,result);
			}
		}
	}


	public static SimActivityInstance getActivityInstance(final SimProcessInstance processInstance, final SimActivity simActivity,int iterationId) {

		Set<SimActivityInstance> result = new HashSet<SimActivityInstance>() ;
		Set<SimActivityInstance> parsed = new HashSet<SimActivityInstance>() ;
		findExistingInstance(processInstance.getStartElemInstances(),simActivity.getName(),result,parsed) ;

		if(result.isEmpty()){
			SimActivityInstance instance = new SimActivityInstance(simActivity, UUID.randomUUID().toString(), processInstance) ;
			if(simActivity.isInCycle()){
				if(simActivity.isEntryNode()){
					instance.setIterationId(iterationId+1) ;
				}else{
					instance.setIterationId(iterationId) ;
				}
			}
			return instance;
		}else{
			if(simActivity.isInCycle()){
				if(simActivity.isEntryNode()){
					SimActivityInstance newInstance =	new SimActivityInstance(simActivity, UUID.randomUUID().toString(), processInstance) ;
					newInstance.setIterationId(iterationId+1) ;
					return newInstance;
				}else{
					for(SimActivityInstance i : result){
						if(i.getIterationId() == iterationId){
							return i ;
						}
					}
					SimActivityInstance newInstance = new SimActivityInstance(simActivity, UUID.randomUUID().toString(), processInstance) ;
					newInstance.setIterationId(iterationId) ;
					return newInstance;
				}
			}else{
				return result.iterator().next();
			}
		}
	}

	public static void findExistingInstance(Set<SimActivityInstance> activityInstances,String name,Set<SimActivityInstance> result,Set<SimActivityInstance> parsed) {
		for(SimActivityInstance a : activityInstances){
			if(a.getDefinition().getName().equals(name)){
				result.add(a);
			}
			parsed.add(a) ;
			if(a.hasNext()){
				for(SimActivityInstance nextInstance : a.getNext()){
					if(!parsed.contains(nextInstance)){
						findExistingInstance(Collections.singleton(nextInstance),name,result,parsed) ;
					}
				}
				
			}
		}

	}

	public static SimProcessInstance createInstance(final SimProcess simProc, long injectionTime) {
		final String uuid = UUID.randomUUID().toString() ;
		final SimProcessInstance spi = new SimProcessInstance(simProc,uuid,injectionTime) ;

		final Set<SimData> data = simProc.getData() ;
		if (!data.isEmpty()){
			for (SimData d : data){
				DefinitionPool.getInstance().addDataDefinition(simProc.getName(), d);
				final Object value = DataUtil.getInstance().generateDataInstance(d);
				final SimDataInstance di = new SimDataInstance(d, uuid, value) ;
				spi.addDataInstance(di) ;
			}
		}
		return spi ;
	}

	public static void removeInstance(Set<SimActivityInstance> activityInstances,SimActivityInstance task) {
		for(SimActivityInstance a : activityInstances){
			if(a.equals(task)){
				a = null ;
				return ;
			}
			if(a.hasNext()){
				removeInstance(a.getNext(),task) ;
			}
		}

	}

}
