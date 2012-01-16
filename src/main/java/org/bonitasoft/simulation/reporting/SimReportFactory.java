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

import java.util.Map;

import org.bonitasoft.simulation.engine.RuntimeResource;
import org.bonitasoft.simulation.model.loadprofile.LoadProfile;

/**
 * @author Romain Bioteau
 *
 */
public class SimReportFactory implements ISimulationReportFactory{

	private String workspace;
	private LoadProfile loadprofile;
	private ISimulationStore store;
	private Map<String, RuntimeResource> resourceInstances;
	private long executionStart;
	private long timespan;
	private long simEndDate;
	private long simStartDate;

	public SimReportFactory(String workspace, 
			LoadProfile lp,
			ISimulationStore store,
			Map<String, RuntimeResource> resourceInstances, 
			long startDate, 
			long endDate, 
			long timespan , 
			long executionStart){
		this.workspace = workspace ;
		this.loadprofile = lp ;
		this.store = store ; 
		this.resourceInstances = resourceInstances ;
		this.simStartDate = startDate ;
		this.simEndDate = endDate ;
		this.timespan = timespan ;
		this.executionStart = executionStart ;
	}
	
	public SimReport createSimulationReport() throws Exception {
		return new JasperSimReport(workspace,loadprofile,store,resourceInstances,simStartDate,simEndDate, timespan,executionStart) ;
	}

}
