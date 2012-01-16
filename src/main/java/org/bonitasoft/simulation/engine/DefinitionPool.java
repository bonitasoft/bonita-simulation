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

import org.bonitasoft.simulation.model.process.SimActivity;
import org.bonitasoft.simulation.model.process.SimData;
import org.bonitasoft.simulation.model.process.SimProcess;
import org.bonitasoft.simulation.model.resource.Resource;

/**
 * @author Romain Bioteau
 *
 */
public class DefinitionPool {

	private static DefinitionPool INSTANCE ;
	private Map<String,Map<String, SimActivity>> activityDefinitions ;
	private Map<String,Map<String, SimData>> dataDefinitions ;
	private Map<String, SimProcess> processDefinitions ;
	private Map<String, Resource> resourceDefinitions ;

	public static DefinitionPool getInstance(){
		if(INSTANCE == null){
			INSTANCE = new DefinitionPool();
		}

		return INSTANCE ;
	}
	
	public static DefinitionPool createInstance(){
		INSTANCE = new DefinitionPool() ;
		return INSTANCE ;
	}
	private DefinitionPool(){
		processDefinitions = new HashMap<String, SimProcess>() ;
		activityDefinitions = new HashMap<String, Map<String, SimActivity>>() ;
		dataDefinitions = new HashMap<String, Map<String, SimData>>() ;
		resourceDefinitions = new HashMap<String, Resource>() ;
	}

	public void addResourceDefinition(Resource definition){
		if(!resourceDefinitions.containsKey(definition.getName())){
			resourceDefinitions.put(definition.getName(), definition);
		}
	}

	public void addProcessDefinition(SimProcess definition){
		if(!processDefinitions.containsKey(definition.getName())){
			processDefinitions.put(definition.getName(), definition);
		}
	}
	
	public void addDataDefinition(String parentProcessName,SimData definition){
		if(dataDefinitions.get(parentProcessName) != null){
			 Map<String, SimData> data =  dataDefinitions.get(parentProcessName) ;
			 if(!data.containsKey(definition.getName())){
				 data.put(definition.getName(), definition);
			 }
		}else{
			 Map<String, SimData> data =  new HashMap<String, SimData>();
			 data.put(definition.getName(), definition) ;
			 dataDefinitions.put(parentProcessName, data);
		}
	}
	
	public void addActivityDefinition(String parentProcessName ,SimActivity definition){
		if(activityDefinitions.get(parentProcessName) != null){
			 Map<String, SimActivity> activities =  activityDefinitions.get(parentProcessName) ;
			 if(!activities.containsKey(definition.getName())){
				 activities.put(definition.getName(), definition);
			 }
		}else{
			 Map<String, SimActivity> activities =  new HashMap<String, SimActivity>();
			 activities.put(definition.getName(), definition) ;
			 activityDefinitions.put(parentProcessName, activities);
		}
	}

	public SimActivity getActivityDefinition(String parentProcessName , String activityName){
		return activityDefinitions.get(parentProcessName).get(activityName);
	}
	
	public SimProcess getProcessDefinition(String processName){
		return processDefinitions.get(processName);
	}

	public SimData getDataDefinition(String parentProcessName, String dataName) {
		return dataDefinitions.get(parentProcessName).get(dataName);
	}

	public Resource getResourceDefinition(String resourceName) {
		return resourceDefinitions.get(resourceName);
	}

	public List<Resource> getResourceDefinitions() {
		return new ArrayList<Resource>(resourceDefinitions.values());
	}

	public List<SimProcess> getProcessDefinition() {
		return new ArrayList<SimProcess>(processDefinitions.values());
	}

	public List<SimData> getDataDefinition(String processName) {
		if(dataDefinitions.get(processName) != null){
			return new ArrayList<SimData>(dataDefinitions.get(processName).values());
		}else{
			return Collections.emptyList() ;
		}
	}

	public List<SimActivity> getActivityDefinition(String processName) {
		if(activityDefinitions.get(processName) != null){
			return new ArrayList<SimActivity>(activityDefinitions.get(processName).values());
		}else{
			return Collections.emptyList() ;
		}
		
	}
	
}
