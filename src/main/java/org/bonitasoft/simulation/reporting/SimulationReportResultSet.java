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
 
package org.bonitasoft.simulation.reporting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Romain Bioteau
 *
 */
public class SimulationReportResultSet {

	private Map<String, ResourceResultSet> resourceResults ;
	private Map<String, DataResultSet> dataResults ;
	private Map<String, ProcessResultSet> processResults ;
	private Map<String, ActivityResultSet> activityResults ;
	
	public SimulationReportResultSet(){
		resourceResults = new HashMap<String, ResourceResultSet>();
		dataResults = new HashMap<String, DataResultSet>();
		processResults = new HashMap<String, ProcessResultSet>();
		activityResults = new HashMap<String, ActivityResultSet>();
	}
	
	public ResourceResultSet getResourceResultSet(String resourceName){
		return resourceResults.get(resourceName);
	}
	
	public ProcessResultSet getProcessResultSet(String processName){
		return processResults.get(processName);
	}
	
	public DataResultSet getDataResultSet(String dataName){
		return dataResults.get(dataName);
	}
	
	public ActivityResultSet getActivityResultSet(String activityName){
		return activityResults.get(activityName);
	}
	
	public void addDataResultSet(String dataName , DataResultSet rSet){
		dataResults.put(dataName, rSet);
	}
	
	public void addActivityResultSet(String activityName , ActivityResultSet rSet){
		activityResults.put(activityName, rSet);
	}
	
	public void addProcessResultSet(String processName , ProcessResultSet rSet){
		processResults.put(processName, rSet);
	}
	
	public void addResourceResultSet(String resourceName , ResourceResultSet rSet){
		resourceResults.put(resourceName, rSet);
	}

	public List<ResourceResultSet> getResourceResultSets() {
		return new ArrayList<ResourceResultSet>(resourceResults.values());
	}
	
	public List<ProcessResultSet> getProcessResultSets() {
		return new ArrayList<ProcessResultSet>(processResults.values());
	}
	
	public List<ActivityResultSet> getActivityResultSets() {
		return new ArrayList<ActivityResultSet>(activityResults.values());
	}
	
	public List<DataResultSet> getDataResultSets() {
		return new ArrayList<DataResultSet>(dataResults.values());
	}
	
}
