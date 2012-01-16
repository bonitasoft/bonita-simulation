package org.bonitasoft.simulation.reporting;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bonitasoft.simulation.engine.DefinitionPool;
import org.bonitasoft.simulation.engine.ResourcePool;
import org.bonitasoft.simulation.engine.RuntimeResource;
import org.bonitasoft.simulation.model.TimeUnit;
import org.bonitasoft.simulation.model.instance.SimActivityInstance;
import org.bonitasoft.simulation.model.instance.SimDataInstance;
import org.bonitasoft.simulation.model.instance.SimProcessInstance;
import org.bonitasoft.simulation.model.loadprofile.LoadProfile;
import org.bonitasoft.simulation.model.process.ResourceAssignement;
import org.bonitasoft.simulation.model.process.SimActivity;
import org.bonitasoft.simulation.model.process.SimData;
import org.bonitasoft.simulation.model.process.SimNumberData;
import org.bonitasoft.simulation.model.process.SimProcess;
import org.bonitasoft.simulation.model.resource.Resource;

public abstract class SimReport {

	protected ISimulationStore store ;
	protected LoadProfile loadProfile;
	protected long simEndDate;
	protected long simStartDate;
	protected long timespan;
	private long nextInterval;
	protected String workspace;
	protected Map<String, RuntimeResource> resourceConso;
	protected long executionStart;

	private static final int INSTANCE_BUFFER_SIZE = 1000;


	public SimReport(String workspace, LoadProfile lp ,ISimulationStore iSimulationStore, long simStartDate,long simEndDate, long timespan, Map<String, RuntimeResource> resourceConso, long executionStart ) throws Exception{
		this.loadProfile = lp ;
		this.store = iSimulationStore ;	
		this.simStartDate = simStartDate ;
		this.simEndDate = simEndDate ;
		this.timespan = timespan ;
		this.workspace = workspace ;
		this.resourceConso = resourceConso;
		this.executionStart =executionStart ;
	}


	public SimulationReportResultSet createSimulationReportResultSet(String processName) throws Exception{

		SimulationReportResultSet result = new SimulationReportResultSet();
		
		Set<String> availableUnit = new HashSet<String>();
		for(Resource r : DefinitionPool.getInstance().getResourceDefinitions()){
			result.addResourceResultSet(r.getName(), new ResourceResultSet(ResourcePool.getInstance().getResourceInstances().get(r.getName())));
			availableUnit.add(r.getCostUnit()) ;
		}
		
		result.addProcessResultSet(processName, new ProcessResultSet(DefinitionPool.getInstance().getProcessDefinition(processName),availableUnit));

	

		for(SimData d : DefinitionPool.getInstance().getProcessDefinition(processName).getData()){
			result.addDataResultSet(d.getName(), new DataResultSet(d)) ;
		}

		for(SimActivity a : DefinitionPool.getInstance().getActivityDefinition(processName)){
			result.addActivityResultSet(a.getName(), new ActivityResultSet(DefinitionPool.getInstance().getProcessDefinition(processName),a)) ;
		}



		nextInterval = simStartDate + timespan ;
		List<SimProcessInstance> instances = store.getStoredFinishedProcessInstances(INSTANCE_BUFFER_SIZE) ;
		while(instances.size() > 0){

			for(SimProcessInstance instance : instances){

				long instanceEnd = instance.getEndDate() ;
				long startDate = instance.getStartDate() ;
				if(instanceEnd >= nextInterval && nextInterval != simEndDate){
					updateProcessResultSet(nextInterval,result) ;
					updateActivityResultSet(nextInterval,result) ;
					updateResourceResultSet(nextInterval,result) ;
					nextInterval = nextInterval + timespan ;
					if(nextInterval > simEndDate){
						nextInterval = simEndDate ;
					}
				}



				Set<SimActivityInstance> parsed = new HashSet<SimActivityInstance>();
				parseProcessInstance(instance.getStartElemInstances(),parsed,result);
				updateDataResultSet(instance, result) ;
				ProcessResultSet rSet = result.getProcessResultSet(processName) ;
				rSet.setInstanceTime(instanceEnd-startDate);


				for(Resource r : DefinitionPool.getInstance().getResourceDefinitions()){
					result.getResourceResultSet(r.getName()).updateResourceConsumption();
					result.getResourceResultSet(r.getName()).updateResourceCost();
					long workableTime = r.getPlanning().getWorkingPlanningDuration(startDate, instanceEnd)*resourceConso.get(r.getName()).getInstances().size() ;
					result.getResourceResultSet(r.getName()).updateResourceUse(workableTime);
				}

				for(SimProcess p : DefinitionPool.getInstance().getProcessDefinition()){
					result.getProcessResultSet(p.getName()).updateCost() ;
					result.getProcessResultSet(p.getName()).updateTime() ;
					result.getProcessResultSet(p.getName()).updateWaitingTime() ;
				}
			}
			instances = store.getStoredFinishedProcessInstances(INSTANCE_BUFFER_SIZE) ;
		}

		updateProcessResultSet(simEndDate,result) ;
		updateActivityResultSet(simEndDate,result) ;
		updateResourceResultSet(simEndDate,result) ;

		for(Resource r : DefinitionPool.getInstance().getResourceDefinitions()){
			result.getResourceResultSet(r.getName()).computeFinalResourceConsumption();
			result.getResourceResultSet(r.getName()).computeFinalResourceCost();
			long workedTime = r.getPlanning().getWorkingPlanningDuration(simStartDate, simEndDate) * resourceConso.get(r.getName()).getInstances().size() ;
			result.getResourceResultSet(r.getName()).computeFinalResourceUse(workedTime);
		}

		for(SimActivity a : DefinitionPool.getInstance().getActivityDefinition(processName)){
			result.getActivityResultSet(a.getName()).computeFinalTime() ;
			result.getActivityResultSet(a.getName()).computeFinalWaitingTime() ;
		}

		for(SimData d : DefinitionPool.getInstance().getProcessDefinition(processName).getData()){
			if(d instanceof SimNumberData){
				result.getDataResultSet(d.getName()).computeFinalValues() ;
			}else{
				result.getDataResultSet(d.getName()).computeFinalRepartion(loadProfile.getTotalInjectedInstances()) ;
			}
		}

		for(SimProcess p : DefinitionPool.getInstance().getProcessDefinition()){
			result.getProcessResultSet(p.getName()).computeFinalCost() ;
			result.getProcessResultSet(p.getName()).computeFinalTime() ;
			result.getProcessResultSet(p.getName()).computeFinalWaitingTime() ;
		}

		return result ;
	}

	private void parseProcessInstance(Set<SimActivityInstance> activityInstances,Set<SimActivityInstance> parsed , SimulationReportResultSet result) {
		for(SimActivityInstance a : activityInstances){
			if(a.getStartDate() != 0){
				parsed.add(a);
				if(a.getFinishDate() > 0){
					updateProcessResultSet(a,result) ;
					updateActivityResultSet(a,result) ;
					updateResourceResultSet(a,result) ;
					if(a.hasNext()){
						for(SimActivityInstance nextInstance : a.getNext()){
							if(!parsed.contains(nextInstance)){
								parseProcessInstance(Collections.singleton(nextInstance),parsed,result) ;
							}
						}
					}
				}
			}
		}
	}



	private void updateResourceResultSet(long instant,SimulationReportResultSet result) {

		for(ResourceResultSet rSet : result.getResourceResultSets()){


			rSet.getAvgCost().put(nextInterval, rSet.getIntermediateCost()) ;

			double useDuration = rSet.getIntermediateUseDuration() ;


			long last = nextInterval - timespan ;

			long workedTime = (long)rSet.getResource().getPlanning().getWorkingPlanningDuration(last, instant) ; 
			if(workedTime > 0){
				double avg = (double) useDuration /(double)  workedTime ;
				double average = (double) avg / resourceConso.get(rSet.getResource().getName()).getInstances().size() ;

				if(average > 1.0){//TODO findout why ?? 
					average = 1.0 ;
				}
				rSet.getAvgUse().put(nextInterval, average) ;


			}else{
				rSet.getAvgUse().put(nextInterval, 0.0) ;
			}


			rSet.reset() ;
		}

	}


	private void updateActivityResultSet(long instant,SimulationReportResultSet result) {
		for(ActivityResultSet rSet : result.getActivityResultSets()){


			rSet.getAvgTime().put(nextInterval, rSet.getIntermediateAvgTime()) ;
			rSet.getMaxTime().put(nextInterval, rSet.getIntermediateMaxTime()) ;

			if(rSet.getIntermediateMinTime() == Long.MAX_VALUE){
				rSet.getMinTime().put(nextInterval,0L) ;
			}else{
				rSet.getMinTime().put(nextInterval, rSet.getIntermediateMinTime()) ;
			}

			rSet.getMedTime().put(nextInterval, rSet.getIntermediateMedTime()) ;

			rSet.getAvgWaitingTime().put(nextInterval, rSet.getIntermediateAvgWaitingTime()) ;
			rSet.getMaxWaitingTime().put(nextInterval, rSet.getIntermediateMaxWaitingTime()) ;
			if(rSet.getIntermediateMinWaitingTime() == Long.MAX_VALUE){
				rSet.getMinWaitingTime().put(nextInterval,0L) ;
			}else{
				rSet.getMinWaitingTime().put(nextInterval, rSet.getIntermediateMinWaitingTime()) ;
			}

			rSet.getMedWaitingTime().put(nextInterval, rSet.getIntermediateMedWaitingTime()) ;


			rSet.reset() ;
		}
	}


	private void updateProcessResultSet(long instant,SimulationReportResultSet result) {

		for(ProcessResultSet rSet : result.getProcessResultSets()){

			rSet.getAvgTime().put(nextInterval, rSet.getIntermediateAvgTime()) ;
			rSet.getMaxTime().put(nextInterval, rSet.getIntermediateMaxTime()) ;

			if(rSet.getTotalMinTime() == Long.MAX_VALUE){
				rSet.getMinTime().put(nextInterval,0L) ;
			}else{
				rSet.getMinTime().put(nextInterval, rSet.getIntermediateMinTime()) ;
			}

			rSet.getMedTime().put(nextInterval, rSet.getIntermediateMedTime()) ;

			rSet.getAvgWaitingTime().put(nextInterval, rSet.getIntermediateAvgWaitingTime()) ;
			rSet.getMaxWaitingTime().put(nextInterval, rSet.getIntermediateMaxWaitingTime()) ;
			if(rSet.getIntermediateMinWaitingTime() == Long.MAX_VALUE){
				rSet.getMinWaitingTime().put(nextInterval,0L) ;
			}else{
				rSet.getMinWaitingTime().put(nextInterval, rSet.getIntermediateMinWaitingTime()) ;
			}

			rSet.getMedWaitingTime().put(nextInterval, rSet.getIntermediateMedWaitingTime()) ;


			rSet.reset() ;
		}

	}


	private void updateDataResultSet(SimProcessInstance instance,SimulationReportResultSet result) {

		for(SimDataInstance d : instance.getDataInstance()){
			DataResultSet rSet = result.getDataResultSet(d.getDefinition().getName()) ;
			if(d.getDefinition() instanceof SimNumberData){
				rSet.addValue((Double) d.getValue()) ;
			}else{
				if(d.getValue() instanceof Boolean){
					rSet.addOcuurence(String.valueOf(d.getValue())) ;
				}else{
					rSet.addOcuurence(d.getValue().toString()) ;
				}
			}
		}
	}


	private void updateResourceResultSet(SimActivityInstance a,SimulationReportResultSet result) {
		for(ResourceAssignement ra : ((SimActivity)a.getDefinition()).getAssignedResources()){
			ResourceResultSet rSet = result.getResourceResultSet(ra.getResource().getName()) ;

			int cons = rSet.getInstanceConsumption() ;
			cons = cons + ra.getQuantity() ;
			rSet.setInstanceConsumption(cons) ;

			double cost = rSet.getInstanceCost() ;
			cost = cost + ra.getResource().getFixedCost() + ( ra.getQuantity()*(ra.getResource().getTimeCost() * (double)ra.getDuration()/(long)getTimeUnitInMillisecond(ra.getResource().getTimeUnit())));
			rSet.setInstanceCost(cost) ;

			long use = rSet.getInstanceUseDuration() ;
			use = use +( ra.getQuantity()* ra.getDuration());
			rSet.setInstanceUseDuration(use) ;
		}

	}



	private void updateActivityResultSet(SimActivityInstance a,SimulationReportResultSet result) {
		ActivityResultSet rSet = result.getActivityResultSet(a.getDefinition().getName()) ;
		long instanceDuration = a.getFinishDate() - a.getStartDate() ;
		long instanceWaintingDuration = instanceDuration - ((SimActivity) a.getDefinition()).getExecutionTime()  ;

		rSet.setInstanceTime(rSet.getInstanceTime() + instanceDuration);
		rSet.setInstanceWaitingTime(rSet.getInstanceWaitingTime() + instanceWaintingDuration);

		rSet.updateTime() ;
		rSet.updateWaitingTime() ;
	}


	private void updateProcessResultSet(SimActivityInstance a,SimulationReportResultSet result) {
		ProcessResultSet rSet = result.getProcessResultSet(((SimActivity)a.getDefinition()).getParentProcessName()) ;

		for(ResourceAssignement ri : ((SimActivity)a.getDefinition()).getAssignedResources()){
			String unit = ri.getResource().getCostUnit() ;
			double cost = ri.getResource().getFixedCost() + (ri.getQuantity()* (ri.getResource().getTimeCost() * (double) ri.getDuration()/(long)getTimeUnitInMillisecond(ri.getResource().getTimeUnit()))); 
			rSet.updateInstanceCost(unit, cost) ;
		}
	}


	private long getTimeUnitInMillisecond(TimeUnit timeUnit) {
		switch(timeUnit){
		case DAY : return 86400000;
		case MINUTE : return 60000;
		case MONTH : return 2629800000L;
		case WEEK : return (long) (86400000*7);
		case HOUR : return 3600000 ;
		case YEAR : return 31557600000L;
		}
		return 0;
	}


	protected abstract String sotreReport(String processName) throws Exception;


	protected abstract void fillResourceSheet(List<ResourceResultSet> resultSets) throws Exception;


	protected abstract void fillLiteralsDataSheet(List<DataResultSet> resultSets) throws Exception;


	protected abstract void fillNumberDataSheet(List<DataResultSet> resultSets) throws Exception;


	protected abstract void fillActivitySheet(List<ActivityResultSet> resultSets) throws Exception;


	protected abstract void fillResourceTimeSheet(List<ResourceResultSet> resultSets) throws Exception;


	protected abstract void fillActivityTimeSheet(List<ActivityResultSet> resultSets) throws Exception;


	protected abstract void fillDataReportSheet(List<DataResultSet> resultSets) throws Exception;


	protected abstract void fillResourceReportSheet() throws Exception;


	protected abstract void fillActivityReportSheet(List<ActivityResultSet> resultSets) throws Exception;


	protected abstract void fillProcessTimeSheet(ProcessResultSet processResultSet) throws Exception;

	protected abstract void fillProcessCostSheet(ProcessResultSet processResultSet) throws Exception;

	protected abstract void fillProcessSheet(ProcessResultSet processResultSet) throws Exception;


	protected abstract void fillLoadProfileSheet() throws Exception;


	protected abstract void fillGeneralSheet() throws Exception;


	public String generate() throws Exception {
		if(!DefinitionPool.getInstance().getProcessDefinition().isEmpty()){
			SimProcess proc = DefinitionPool.getInstance().getProcessDefinition().get(0) ;
			SimulationReportResultSet resultSet = createSimulationReportResultSet(proc.getName()) ;

			fillResourceReportSheet() ;
			fillGeneralSheet();
			fillLoadProfileSheet() ;
			fillProcessSheet(resultSet.getProcessResultSet(proc.getName())) ;
			fillProcessTimeSheet(resultSet.getProcessResultSet(proc.getName()));
			fillProcessCostSheet(resultSet.getProcessResultSet(proc.getName()));
			fillResourceSheet(resultSet.getResourceResultSets());
			fillResourceTimeSheet(resultSet.getResourceResultSets());
			fillActivityReportSheet(resultSet.getActivityResultSets());
			fillActivitySheet(resultSet.getActivityResultSets());
			fillActivityTimeSheet(resultSet.getActivityResultSets());
			if(!resultSet.getDataResultSets().isEmpty()){
				fillDataReportSheet(resultSet.getDataResultSets()) ;
				fillNumberDataSheet(resultSet.getDataResultSets());
				fillLiteralsDataSheet(resultSet.getDataResultSets());
			}
			return sotreReport(proc.getName()) ; 
		}

		return null ;
	}

}
