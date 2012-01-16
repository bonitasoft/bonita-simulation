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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Set;

import org.bonitasoft.simulation.model.Period;
import org.bonitasoft.simulation.model.RepartitionType;
import org.bonitasoft.simulation.model.calendar.SimCalendar;
import org.bonitasoft.simulation.model.instance.RuntimeTask;
import org.bonitasoft.simulation.model.instance.SimActivityInstance;
import org.bonitasoft.simulation.model.instance.SimProcessInstance;
import org.bonitasoft.simulation.model.loadprofile.InjectionPeriod;
import org.bonitasoft.simulation.model.loadprofile.LoadProfile;
import org.bonitasoft.simulation.model.process.JoinType;
import org.bonitasoft.simulation.model.process.SimActivity;
import org.bonitasoft.simulation.model.process.SimProcess;
import org.bonitasoft.simulation.model.process.SimTransition;
import org.bonitasoft.simulation.model.resource.Resource;
import org.bonitasoft.simulation.reporting.CSVSimReportStorage;
import org.bonitasoft.simulation.reporting.ISimulationStore;
import org.bonitasoft.simulation.reporting.SimReport;
import org.bonitasoft.simulation.reporting.SimReportFactory;
import org.ow2.bonita.facade.def.element.impl.IterationDescriptor;
import org.ow2.bonita.iteration.IterationDetection;
import org.ow2.bonita.iteration.IterationNode;
import org.ow2.bonita.iteration.IterationProcess;
import org.ow2.bonita.iteration.IterationTransition;

/**
 * @author Romain Bioteau
 *
 */
public class SimulationEngine {


	public final static String MAXIMUM_DELAY_ACTIVITY = "maxDelayProperty"; //$NON-NLS-1$
	public final static String REPORT_WORKSPACE = "reportWorkspace"; //$NON-NLS-1$
	public final static String REPORT_TIMESPAN = "timespan"; //$NON-NLS-1$
	public static final String FLUSH_STORE = "flushStore"; //$NON-NLS-1$
	public static final String EXPORT_MODE = "exportMode";

	public static final String HTML_MODE = "HTML";
	public static final String PDF_MODE = "PDF";
	public static final String EXCEL_MODE = "EXCEL";
	
	public static long currentTime = 0;

	private static final int INSTANCE_BUFFER_SIZE = 100 ;

	public static  boolean isStopped = false ;
	private int nbOfLoopWhileUnavailable = 0 ;
	private PriorityQueue<RuntimeTask> todoList;
	private LoadProfile loadProfile;
	private SimProcess simulationProcess;
	private ISimulationStore store ;
	private long simulationStartDate ;
	private long simulationEndDate ;
	private String workspace;
	private long timespan;
	private List<Resource> inputResources;
	private long nextInterval;
	private int executedInstance = 0;
	private long executionStart;
	private boolean flushStore = true ;
	private String reportFile;
	private boolean isGeneratingReport = false;
	private int totalInstances = 0;



	public SimulationEngine(SimProcess simulationProcess, LoadProfile loadProfile,List<Resource> inputResources,Properties executionProperties) throws IOException{
		currentTime = 0 ;
		isStopped = false ;
		this.simulationProcess = simulationProcess ;
		this.loadProfile = loadProfile ;
		this.inputResources = inputResources ;

		if(executionProperties != null && executionProperties.get(MAXIMUM_DELAY_ACTIVITY) != null){
			nbOfLoopWhileUnavailable = (Integer)executionProperties.get(MAXIMUM_DELAY_ACTIVITY) ;
		}

		if(executionProperties != null && executionProperties.get(REPORT_WORKSPACE) != null){
			this.workspace = (String)executionProperties.get(REPORT_WORKSPACE) ;
			if(executionProperties != null && executionProperties.get(FLUSH_STORE) != null){
				this.flushStore  = (Boolean) executionProperties.get(FLUSH_STORE) ;
			}
			this.store = new CSVSimReportStorage(workspace,simulationProcess.getName(),flushStore) ;
		}

		if(executionProperties != null && executionProperties.get(REPORT_TIMESPAN) != null){
			this.timespan = (Long) executionProperties.get(REPORT_TIMESPAN) ;
		}
		
		if(timespan == 0){
			timespan = (long)	loadProfile.getTotalDuration() / (long) 12 ;
		}


		DefinitionPool.createInstance() ;
		DataUtil.createInstance() ;

	}

	public void start() throws Exception {
		executionStart = System.currentTimeMillis() ;

		System.err.println("Detecting Cycles"); //$NON-NLS-1$
		detectCycles();
		System.err.println(simulationProcess.getCycles().size()+" Cycles found"); //$NON-NLS-1$

		System.err.println("Create TodoList"); //$NON-NLS-1$
		createTodoList() ;//CREATE INJECTION CALENDAR
		this.todoList = new PriorityQueue<RuntimeTask>();
		List<SimProcessInstance> instances = store.getStoredProcessInstances(INSTANCE_BUFFER_SIZE) ;
		for(SimProcessInstance instance : instances){
			for(SimActivityInstance startActivity : instance.getStartElemInstances()){
				todoList.add(new RuntimeTask(startActivity,instance.getStartDate()));
			}
		}

		simulationStartDate = this.todoList.peek().getStartDate() ;


		currentTime = simulationStartDate ;
		ResourcePool.getInstance().createResources(simulationProcess,inputResources, currentTime,timespan);
		long nextInjection = ((RuntimeTask) todoList.toArray()[todoList.size()-1] ).getStartDate();

		nextInterval = simulationStartDate + timespan ;
		System.err.println("Started"); //$NON-NLS-1$
		long previous = System.currentTimeMillis();	
		while(!isStopped && !todoList.isEmpty()){

			RuntimeTask taskTodo = todoList.poll() ;
			currentTime = taskTodo.getStartDate() ;

			if(currentTime >= nextInjection){//LOAD TODOLIST FROM STORE
				instances = store.getStoredProcessInstances(INSTANCE_BUFFER_SIZE) ;
				if(!instances.isEmpty()){
					for(SimProcessInstance instance : instances){
						for(SimActivityInstance startActivity : instance.getStartElemInstances()){
							todoList.offer(new RuntimeTask(startActivity,instance.getStartDate()));
						}
					}
					nextInjection = instances.get(instances.size()-1).getStartDate();
				}else{
					nextInjection = Long.MAX_VALUE ;
				}
			}


			if(currentTime >= nextInterval){//UPDATE RESOURCE COUNT
				System.err.println("Process instance finished = " + executedInstance + ", executed in " + (System.currentTimeMillis() - previous)); //$NON-NLS-1$ //$NON-NLS-2$
				executedInstance = 0 ;
				previous = System.currentTimeMillis();
				ResourcePool.getInstance().updateResourceConsumption(nextInterval) ;
				nextInterval = nextInterval + timespan ;
			}

			if(taskTodo.getTask().getStartDate() == 0){
				taskTodo.getTask().setStartDate(currentTime);
			}

			SimActivity activity = (SimActivity) taskTodo.getTask().getDefinition() ;

			boolean waiting = false ;
			if(activity.getJoinType().equals(JoinType.AND)){
				if(taskTodo.getTask().getIncomings() != activity.getIncomingTransitions().size()){//WAIT FOR THE OTHERS ACTIVTY
					waiting = true ;
					taskTodo.setStartDate(Long.MAX_VALUE) ;
					todoList.offer(taskTodo);
				}
			}else if(activity.getJoinType().equals(JoinType.XOR)){
				if(taskTodo.getTask().getIncomings() > 1){//SKIP THE EXECUTION
					waiting = true ;
				}
			}

			if(!waiting){
				if (!activity.hasResources()) {
					if(activity.getJoinType().equals(JoinType.XOR)){
						taskTodo.getTask().addIncoming(); 
						if(taskTodo.getTask().getIncomings() == 1){//SKIP THE EXECUTION
							executeActivityInstances(currentTime, taskTodo,null);
						}
					}else{
						executeActivityInstances(currentTime, taskTodo,null);
					}
				} else {

					final Set<ResourceInstanceAvailability> availableResources = ResourcePool.getInstance().getNextAvailableDateForAllResources(taskTodo.getTask().getProcessInstance().getInstanceUUID(),activity.getName(),currentTime, activity.getAssignedResources(), activity.isContigous());
					final long nextAvailableDate = availableResources.iterator().next().getTime();
					if ( nextAvailableDate == currentTime || nbOfLoopWhileUnavailable <= taskTodo.getTask().getSkip() ) {
						if(activity.getJoinType().equals(JoinType.XOR)){
							taskTodo.getTask().addIncoming(); 
							if(taskTodo.getTask().getIncomings() == 1){//SKIP THE EXECUTION
								executeActivityInstances(nextAvailableDate, taskTodo,availableResources);
							}
						}else{
							executeActivityInstances(nextAvailableDate, taskTodo,availableResources);
						}
					} else {
						taskTodo.getTask().skip() ;
						taskTodo.setStartDate(nextAvailableDate);
						todoList.offer(taskTodo);
					}

				}
			}
		}
		
		ResourcePool.getInstance().updateResourceConsumption(getSimulationEndDate()) ;

		if(!isStopped){
			isGeneratingReport = true ;
			SimReport report = new SimReportFactory(workspace,getLoadProfile(), getStore(),ResourcePool.getInstance().getResourceInstances(), getSimulationStartDate(), getSimulationEndDate(), timespan,executionStart).createSimulationReport() ;
			reportFile = report.generate() ;
		}
		currentTime = 0 ;
		getStore().closeStore();
	}

	private void detectCycles() {

		final IterationProcess iterationProcess = new IterationProcess();
		for (SimActivity activityDefinition : simulationProcess.getActivities()) {
			final String joinType = activityDefinition.getJoinType().toString();

			final IterationNode node = new IterationNode(activityDefinition.getName(), 
					org.ow2.bonita.iteration.IterationNode.JoinType.valueOf(joinType), 
					org.ow2.bonita.iteration.IterationNode.SplitType.XOR);
			iterationProcess.addNode(node);
		}

		for (SimActivity activityDefinition : simulationProcess.getActivities()) {
			final IterationNode node = iterationProcess.getNode(activityDefinition.getName());

			for (SimTransition transition : activityDefinition.getIncomingTransitions()) {
				final IterationNode source = iterationProcess.getNode(transition.getSource().getName());
				node.addIncomingTransition(new IterationTransition(source, node));
			}

			for (SimTransition transition : activityDefinition.getOutgoingTransitions()) {
				final IterationNode destination = iterationProcess.getNode(transition.getTarget().getName());
				node.addOutgoingTransition(new IterationTransition(node, destination));
			}
		}

		Set<IterationDescriptor> iterationDescriptors = IterationDetection.findIterations(iterationProcess);

		// update process
		for (final IterationDescriptor iterationDescriptor : iterationDescriptors) {
			simulationProcess.addCycle(iterationDescriptor);
			for (String activityName : iterationDescriptor.getCycleNodes()) {
				final SimActivity activity = simulationProcess.getActivity(activityName);
				activity.setInCycle(true);
			}
			for (String activityName : iterationDescriptor.getEntryNodes()) {
				final SimActivity activity = simulationProcess.getActivity(activityName);
				activity.setEntryNode(true);
			}
			for (String activityName : iterationDescriptor.getExitNodes()) {
				final SimActivity activity = simulationProcess.getActivity(activityName);
				activity.setExitNode(true);
			}
		}
	}

	public synchronized void stop(){
		isStopped = true ;
	}

	public void executeActivityInstances(long currentDate,RuntimeTask taskTodo,Set<ResourceInstanceAvailability> resourceInstances) throws Exception {

		boolean skipExecution = false ;
		SimActivity activity = (SimActivity) taskTodo.getTask().getDefinition() ;
		long end = 0 ;
		if(resourceInstances != null){
			for(ResourceInstanceAvailability r : resourceInstances){
				Period p = new Period(r.getTime(), r.getTime() + r.getDuration()) ;
				List<Period> periods = r.getResource().getPlanning().split(p) ;
				long inAddition = activity.getExecutionTime() - r.getDuration() ;
				if(!periods.isEmpty()){
					Period latestPeriod = Collections.min(periods);
					if(end  < (latestPeriod.getEnd()+inAddition)){
						end = latestPeriod.getEnd() + inAddition ;
					}
				}
			}
		}

		if(end == 0){
			end = currentDate + activity.getExecutionTime();
		}

		List<SimActivityInstance> availableActivities = getNextActivities(taskTodo.getTask());
		//ADD NEXT ACTIVITIES TO TODOLIST
		for(SimActivityInstance nextActivityInstance : availableActivities){

			RuntimeTask toRemove = null ;
			boolean founded = false ;

			if(nextActivityInstance.getExecutionDate() != 0){
				skipExecution = true ;
			}

			if(!skipExecution){
				for(RuntimeTask t : todoList){
					if(t.getTask().equals(nextActivityInstance)){
						founded = true ;
						toRemove = t ;
						break;
					}	
				}
			}

			if(founded){
				if(((SimActivity) nextActivityInstance.getDefinition()).getJoinType().equals(JoinType.XOR) && nextActivityInstance.getStartDate() <= end ){
					skipExecution = true ;
				}else{
					todoList.remove(toRemove);
					RuntimeTask newTask = new RuntimeTask(nextActivityInstance, toRemove.getStartDate()) ;
					toRemove = null ;


					if(((SimActivity) newTask.getTask().getDefinition()).getJoinType().equals(JoinType.AND)){//KEEP THE LATEST
						if(newTask.getStartDate() == Long.MAX_VALUE || newTask.getStartDate() < end){
							newTask.setStartDate(end) ;
						}
						newTask.getTask().addIncoming(); 
					}else{
						if(newTask.getStartDate() > end){
							newTask.setStartDate(end) ;
						}
					}

					todoList.offer(newTask);
				}
			}else{
				if(((SimActivity) nextActivityInstance.getDefinition()).getJoinType().equals(JoinType.AND)){
					nextActivityInstance.addIncoming(); 
				}
				todoList.offer(new RuntimeTask(nextActivityInstance, end)) ;	
			}

			taskTodo.getTask().addNext(nextActivityInstance) ;
		}

		if(!skipExecution){
			taskTodo.getTask().setExecutionDate(currentDate);
			taskTodo.getTask().setFinishDate(end) ;

			if(resourceInstances != null && !resourceInstances.isEmpty()){
				ResourcePool.getInstance().lockAvailableResourceInstances(taskTodo.getTask(), activity.getAssignedResources(), activity.isContigous(), resourceInstances); //LOCK INSTANCES
			}

			DataUtil.getInstance().evaluateActivityData((SimActivityInstance) taskTodo.getTask()); //EVALUATE DATA

			if(availableActivities.isEmpty()){
				executedInstance  ++ ;
				taskTodo.getTask().getProcessInstance().setEndDate(end);

				List<RuntimeTask> toRemove = new ArrayList<RuntimeTask>() ;
				for(RuntimeTask t : todoList){
					if(t.getTask().getProcessInstance().equals(taskTodo.getTask().getProcessInstance())){
						if(t.getTask().getExecutionDate() == 0){
							toRemove.add(t);
						}
					}
				}
				if(!toRemove.isEmpty()){
					todoList.removeAll(toRemove) ;
				}
				totalInstances ++ ;
				for(String res : ResourcePool.getInstance().getResourceInstances().keySet()){
					ResourcePool.getInstance().getResourceInstances().get(res).updateProcessInstanceWaitingFor(taskTodo.getTask().getProcessInstance().getInstanceUUID()) ;
				}
				
				store.storeFinishedProcessInstance(taskTodo.getTask().getProcessInstance()) ;
			}
			taskTodo = null ;
			simulationEndDate = end;
		}

	}

	/**
	 * Return the next activity to perform who potential takes time
	 * e.g. : Task and Intermediate Event
	 * @param taskTodo
	 * @return the activity to perform
	 * @throws Exception 
	 */
	public List<SimActivityInstance> getNextActivities(SimActivityInstance elem) throws Exception {
		final List<SimActivityInstance> results = new ArrayList<SimActivityInstance>();
		final SimTransition[] transitions = (SimTransition[]) ((SimActivity) elem.getDefinition()).getOutgoingTransitions().toArray(new SimTransition[0]) ;
		if(transitions.length != 0 && !(transitions[0].isDataBased())){//Proba-based Transitions
			if(((SimActivity) elem.getDefinition()).isExclusiveOutgoingTransition()){
				//EXCLUSIVE TRANSITIONS PROBABILITY
				List<List<Integer>> probabilities = new ArrayList<List<Integer>>();
				for(int i = 0 ; i<transitions.length ; i++){
					List<Integer> p = new ArrayList<Integer>();
					for(int j = (int) (i > 0 ? transitions[i-1].getProbability()*100 : 0) ; j < (i > 0 ? probabilities.get(i-1).size()+transitions[i].getProbability()*100 : transitions[i].getProbability()*100); j++){
						p.add(j) ;
					}
					probabilities.add(p);
				}

				int r = DataUtil.getInstance().getRandom().nextInt(100) ;
				for(int i = 0 ; i< probabilities.size() ; i++ ){
					if(probabilities.get(i).contains(r)){
						results.add(SimulationHelper.getActivityInstance(elem.getProcessInstance(),transitions[i].getTarget(),elem.getIterationId()));
					}
				}
				if(results.isEmpty()){
					results.add(SimulationHelper.getActivityInstance(elem.getProcessInstance(),transitions[transitions.length-1].getTarget(),elem.getIterationId())) ;
				}
			}else{
				//UNEXCLUSIVE TRANSITIONS PROBABILITY
				for(SimTransition t : transitions){

					boolean choice = (DataUtil.getInstance().getRandom().nextInt(100)<t.getProbability()*100) ? true : false;
					if(choice){
						results.add(SimulationHelper.getActivityInstance(elem.getProcessInstance(), t.getTarget(),elem.getIterationId()));
					}
				}
			}
		}else if(transitions.length != 0 && transitions[0].isDataBased()){
			for(SimTransition t : transitions){
				if(DataUtil.getInstance().evaluateTransition(elem.getProcessInstance(),t)){
					results.add(SimulationHelper.getActivityInstance(elem.getProcessInstance(),t.getTarget(),elem.getIterationId()));
				}
			}
		}


		return results;
	}

	/**
	 * Create the todo task list, this list is an sorted list with the first element representing the first task todo
	 * @throws Exception 
	 */
	public void createTodoList() throws Exception{


		LoadProfile lp = getLoadProfile() ;
		SimCalendar offCalendar = lp.getInjectionCalendar() ;

		for(InjectionPeriod p : lp.getInjectionPeriods()){
			int nbToInject = p.getNumberOfInstance() ;
			RepartitionType type = p.getRepartition() ;
			long start = p.getPeriod().getBegin() ;
			long end = p.getPeriod().getEnd() ;

			if(type.equals(RepartitionType.CONSTANT)){

				long workingDuration = lp.getInjectionCalendar().getWorkingPlanningDuration(start,end) ;
				long interval = workingDuration / nbToInject ;
				int injected = 0 ;
				long current = 0 ; 
				long lastInjectionTime = start ;
				while(!isStopped && injected < nbToInject && workingDuration > 0){
					long injectionTime = offCalendar.getWorkingDurationEndDate(lastInjectionTime,interval) ;
					SimProcessInstance instance = SimulationHelper.createInstance(getSimulationProcess(),injectionTime);
					for(SimActivity startActivity : ((SimProcess) instance.getDefinition()).getStartElements()){
						SimActivityInstance activityInstance = SimulationHelper.getActivityInstance(instance, startActivity,0) ;
						if(activityInstance == null){
							System.out.println(activityInstance);
						}
						instance.addStartElement(activityInstance);
					}
					store.storeProcessInstance(instance);

					injected++ ;
					lastInjectionTime = injectionTime ;
					current = current + interval ;
					workingDuration = workingDuration - interval ;
				}

			}else if(type.equals(RepartitionType.DIRECT)){
				int injected = 0 ;
				long injectionTime = offCalendar.getNextPlanningAvailable(start) ;
				while(!isStopped && injected < nbToInject){
					SimProcessInstance instance = SimulationHelper.createInstance(getSimulationProcess(),injectionTime);
					for(SimActivity startActivity : ((SimProcess) instance.getDefinition()).getStartElements()){
						SimActivityInstance activity = SimulationHelper.getActivityInstance(instance, startActivity,0) ;
						instance.addStartElement(activity);
					}
					store.storeProcessInstance(instance);
					injected++ ;
				}
			}
		}

	}

	public LoadProfile getLoadProfile() {
		return loadProfile;
	}

	public SimProcess getSimulationProcess(){
		return simulationProcess ;
	}

	public PriorityQueue<RuntimeTask> getTodoList(){
		return todoList ;
	}

	public void setTodoList(PriorityQueue<RuntimeTask> todoList){
		this.todoList = todoList ;
	}

	public ISimulationStore getStore() {
		return store ;
	}


	public long getSimulationStartDate() {
		return simulationStartDate;
	}

	public long getSimulationEndDate() {
		return simulationEndDate;
	}

	public String getReportFile() {
		return reportFile;
	}

	public boolean isStopped(){
		return isStopped ;
	}

	public boolean isGeneratingReport(){
		return isGeneratingReport  ;
	}
	
	public int getTotalInstances() {
		return totalInstances;
	}
}
