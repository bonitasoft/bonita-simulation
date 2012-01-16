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

package org.bonitasoft.simulation;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Set;

import junit.framework.TestCase;

import org.bonitasoft.simulation.engine.ResourcePool;
import org.bonitasoft.simulation.engine.RuntimeResource;
import org.bonitasoft.simulation.engine.SimulationEngine;
import org.bonitasoft.simulation.model.Period;
import org.bonitasoft.simulation.model.RepartitionType;
import org.bonitasoft.simulation.model.TimeUnit;
import org.bonitasoft.simulation.model.instance.ResourceInstance;
import org.bonitasoft.simulation.model.instance.RuntimeTask;
import org.bonitasoft.simulation.model.instance.SimActivityInstance;
import org.bonitasoft.simulation.model.instance.SimProcessInstance;
import org.bonitasoft.simulation.model.loadprofile.InjectionPeriod;
import org.bonitasoft.simulation.model.loadprofile.LoadProfile;
import org.bonitasoft.simulation.model.process.JoinType;
import org.bonitasoft.simulation.model.process.SimActivity;
import org.bonitasoft.simulation.model.process.SimProcess;
import org.bonitasoft.simulation.model.resource.Resource;
import org.bonitasoft.simulation.reporting.CSVSimReportStorage;


/**
 * @author Romain Bioteau
 *
 */
public class TestSimulation extends TestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Thread.sleep(10000) ;
	}
	
	public void testHourScaleExecutionLowDensityProfileNoDelay() throws Exception {

		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 0) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ;
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, SimulationTestUtil.getHoursIntoMilliseconds(24)*7*4) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;

		final SimulationEngine engine = new SimulationEngine(SimulationTestUtil.createProcessDefinition(),SimulationTestUtil.createLoadProfileWithLowDensity(),SimulationTestUtil.createResources(),executionProperties); 
		engine.start();

		validateExecution(engine);
		
	}

	public void testCycleProcess() throws Exception {

		List<Resource> ressources = new ArrayList<Resource>();
		Resource employee = new Resource("R&D Team","Developer",40,40,SimulationTestUtil.createWorkingWeekCalendar(),"$",TimeUnit.HOUR,0,20);
		Resource saler = new Resource("Sales Team","Saler",30,20,SimulationTestUtil.createWorkingWeekCalendar(),"$",TimeUnit.HOUR,0,25);
		Resource trucks = new Resource("Truck","Truck",30,00,SimulationTestUtil.createWorkingWeekCalendar(),"$",TimeUnit.DAY,0,40);
		Resource it = new Resource("IT","IT",30,20,SimulationTestUtil.createWorkingWeekCalendar(),"$",TimeUnit.HOUR,0,15);

		ressources.add(employee);
		ressources.add(saler);
		ressources.add(trucks);
		ressources.add(it) ;

		SimProcess proc = SimulationTestUtil.createCycleProc() ;


		Calendar myInstance = GregorianCalendar.getInstance() ;
		List<InjectionPeriod> injections = new ArrayList<InjectionPeriod>();

		myInstance.set(2010, 6, 1, 0, 0);
		long p1Start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.MONTH, 3) ;
		long p1End = myInstance.getTimeInMillis() ;
		Period period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,80,RepartitionType.CONSTANT));


		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,180,RepartitionType.CONSTANT));

		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,100,RepartitionType.CONSTANT));

		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,80,RepartitionType.CONSTANT));

		LoadProfile lp = new LoadProfile(SimulationTestUtil.createWorkingWeekCalendar(), injections);


		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 0) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ; 
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, 30*SimulationTestUtil.getHoursIntoMilliseconds(24)) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;
		executionProperties.put(SimulationEngine.EXPORT_MODE, SimulationEngine.PDF_MODE) ;

		try{
			final SimulationEngine engine = new SimulationEngine(proc,lp,ressources,executionProperties); 
			engine.start();
		}catch (Exception e) {
			return ;
		}
		fail() ;
		
	}
	
	public void testXorProcess() throws Exception {

		List<Resource> ressources = new ArrayList<Resource>();
		Resource employee = new Resource("R&D Team","Developer",40,40,SimulationTestUtil.createWorkingWeekCalendar(),"$",TimeUnit.HOUR,0,20);
		Resource saler = new Resource("Sales Team","Saler",30,20,SimulationTestUtil.createWorkingWeekCalendar(),"$",TimeUnit.HOUR,0,25);
		Resource trucks = new Resource("Truck","Truck",30,00,SimulationTestUtil.createWorkingWeekCalendar(),"$",TimeUnit.DAY,0,40);
		Resource it = new Resource("IT","IT",30,20,SimulationTestUtil.createWorkingWeekCalendar(),"$",TimeUnit.HOUR,0,15);

		ressources.add(employee);
		ressources.add(saler);
		ressources.add(trucks);
		ressources.add(it) ;

		SimProcess proc = SimulationTestUtil.createXorProc() ;


		Calendar myInstance = GregorianCalendar.getInstance() ;
		List<InjectionPeriod> injections = new ArrayList<InjectionPeriod>();

		myInstance.set(2010, 6, 1, 0, 0);
		long p1Start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.MONTH, 3) ;
		long p1End = myInstance.getTimeInMillis() ;
		Period period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,80,RepartitionType.CONSTANT));


		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,180,RepartitionType.CONSTANT));

		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,100,RepartitionType.CONSTANT));

		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,80,RepartitionType.CONSTANT));

		LoadProfile lp = new LoadProfile(SimulationTestUtil.createWorkingWeekCalendar(), injections);


		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 0) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ; 
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, 30*SimulationTestUtil.getHoursIntoMilliseconds(24)) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;


		final SimulationEngine engine = new SimulationEngine(proc,lp,ressources,executionProperties); 
		engine.start();

		validateExecution(engine);
	}
	
	public void testANDProcess() throws Exception {


		SimProcess proc = SimulationTestUtil.createANDProc() ;


		Calendar myInstance = GregorianCalendar.getInstance() ;
		List<InjectionPeriod> injections = new ArrayList<InjectionPeriod>();

		myInstance.set(2010, 6, 1, 0, 0);
		long p1Start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.MONTH, 3) ;
		long p1End = myInstance.getTimeInMillis() ;
		Period period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,1,RepartitionType.CONSTANT)) ;


		LoadProfile lp = new LoadProfile(SimulationTestUtil.createWorkingWeekCalendar(), injections);


		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 0) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ; 
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, 30*SimulationTestUtil.getHoursIntoMilliseconds(24)) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;


		final SimulationEngine engine = new SimulationEngine(proc,lp,new ArrayList<Resource>(),executionProperties); 
		engine.start();

		
		validateExecution(engine);
	}
	
	public void testRealProcess() throws Exception {


		SimProcess proc = SimulationTestUtil.createRealProc() ;

		Calendar myInstance = GregorianCalendar.getInstance() ;
		List<InjectionPeriod> injections = new ArrayList<InjectionPeriod>();

		myInstance.set(2010, 6, 1, 0, 0);
		long p1Start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.MONTH, 3) ;
		long p1End = myInstance.getTimeInMillis() ;
		Period period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,800,RepartitionType.CONSTANT));


		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,1800,RepartitionType.CONSTANT));

		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,1000,RepartitionType.CONSTANT));

		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,800,RepartitionType.CONSTANT));

		LoadProfile lp = new LoadProfile(SimulationTestUtil.createWorkingWeekCalendar(), injections);


		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 0) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ; 
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, 30*SimulationTestUtil.getHoursIntoMilliseconds(24)) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;


		final SimulationEngine engine = new SimulationEngine(proc,lp,SimulationTestUtil.createRealResources(),executionProperties); 
		engine.start();

		validateExecution(engine);
	}
	
	public void testRealProcessWithUnlimitedRDResource() throws Exception {


		SimProcess proc = SimulationTestUtil.createRealProcWithUnlimitedRD() ;

		Calendar myInstance = GregorianCalendar.getInstance() ;
		List<InjectionPeriod> injections = new ArrayList<InjectionPeriod>();

		myInstance.set(2010, 6, 1, 0, 0);
		long p1Start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.MONTH, 3) ;
		long p1End = myInstance.getTimeInMillis() ;
		Period period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,800,RepartitionType.CONSTANT));


		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,1800,RepartitionType.CONSTANT));

		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,1000,RepartitionType.CONSTANT));

		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,800,RepartitionType.CONSTANT));

		LoadProfile lp = new LoadProfile(SimulationTestUtil.createWorkingWeekCalendar(), injections);


		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 0) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ; 
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, 30*SimulationTestUtil.getHoursIntoMilliseconds(24)) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;


		final SimulationEngine engine = new SimulationEngine(proc,lp,SimulationTestUtil.createRealResourcesWithUnlimitedRD(),executionProperties); 
		engine.start();

		validateExecution(engine);
	}
	
	
	public void testRealProcessFullWorking() throws Exception {

		List<Resource> ressources = new ArrayList<Resource>();
		Resource employee = new Resource("R&D Team","Developer",40,00,SimulationTestUtil.createWorkingAllWeekCalendar(),"$",TimeUnit.HOUR,0,20);
		Resource saler = new Resource("Sales Team","Saler",30,20,SimulationTestUtil.createWorkingAllWeekCalendar(),"$",TimeUnit.HOUR,0,25);
		Resource trucks = new Resource("Truck","Truck",30,20,SimulationTestUtil.createWorkingAllWeekCalendar(),"$",TimeUnit.DAY,0,40);
		Resource it = new Resource("IT","IT",30,20,SimulationTestUtil.createWorkingAllWeekCalendar(),"$",TimeUnit.HOUR,0,15);

		ressources.add(employee);
		ressources.add(saler);
		ressources.add(trucks);
		ressources.add(it) ;

		SimProcess proc = SimulationTestUtil.createRealProc() ;


		Calendar myInstance = GregorianCalendar.getInstance() ;
		List<InjectionPeriod> injections = new ArrayList<InjectionPeriod>();

		myInstance.set(2010, 6, 1, 0, 0);
		long p1Start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.MONTH, 3) ;
		long p1End = myInstance.getTimeInMillis() ;
		Period period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,800,RepartitionType.CONSTANT));


		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,1800,RepartitionType.CONSTANT));

		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,1000,RepartitionType.CONSTANT));

		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,800,RepartitionType.CONSTANT));

		LoadProfile lp = new LoadProfile(SimulationTestUtil.createWorkingAllWeekCalendar(), injections);


		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 0) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ; 
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, 30*SimulationTestUtil.getHoursIntoMilliseconds(24)) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;


		final SimulationEngine engine = new SimulationEngine(proc,lp,ressources,executionProperties); 
		engine.start();

		validateExecution(engine);
	}

	private void validateExecution(final SimulationEngine engine)
			throws Exception {
		File[] foundFiles =  new File("target").listFiles(new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				return 	(dir.getAbsolutePath()+File.separatorChar+name).startsWith(((CSVSimReportStorage)engine.getStore()).getStoredInstanceFilename()) || (dir.getAbsolutePath()+File.separatorChar+name).startsWith(((CSVSimReportStorage)engine.getStore()).getFinishedStoredInstanceFilename());
			}
		});
		
		assertEquals(2,foundFiles.length) ;
		
		for(File f: foundFiles){
			if(f.getAbsolutePath().startsWith(((CSVSimReportStorage)engine.getStore()).getStoredInstanceFilename())){
				f.renameTo(new File(((CSVSimReportStorage)engine.getStore()).getStoredInstanceFilename())) ;
			}
			if(f.getAbsolutePath().startsWith(((CSVSimReportStorage)engine.getStore()).getFinishedStoredInstanceFilename())){
				f.renameTo(new File(((CSVSimReportStorage)engine.getStore()).getFinishedStoredInstanceFilename())) ;
			}
		}
		
		checkWorkingPeriodsNotOverlapsEachOther(engine);
		checkInstancesConsistency(engine);
		for(File f: foundFiles){
			f.delete();
		}
	}	
	
	public void testRealProcessWithData() throws Exception {

		List<Resource> resources = SimulationTestUtil.createRealResources();
		SimProcess proc = SimulationTestUtil.createRealProcWithData() ;


		Calendar myInstance = GregorianCalendar.getInstance() ;
		List<InjectionPeriod> injections = new ArrayList<InjectionPeriod>();

		myInstance.set(2010, 6, 1, 0, 0);
		long p1Start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.MONTH, 3) ;
		long p1End = myInstance.getTimeInMillis() ;
		Period period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,800,RepartitionType.CONSTANT));


		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,1800,RepartitionType.CONSTANT));

		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,1000,RepartitionType.CONSTANT));

		p1Start = p1End +1 ;
		myInstance.setTimeInMillis(p1Start);
		myInstance.add(Calendar.MONTH, 3) ;
		p1End = myInstance.getTimeInMillis() ;
		period1 = new Period(p1Start, p1End) ;

		injections.add(new InjectionPeriod(period1,800,RepartitionType.CONSTANT));

		LoadProfile lp = new LoadProfile(SimulationTestUtil.createWorkingWeekCalendar(), injections);


		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 0) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ; 
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, 30*SimulationTestUtil.getHoursIntoMilliseconds(24)) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;


		SimulationEngine engine = new SimulationEngine(proc,lp,resources,executionProperties); 
		engine.start();

		validateExecution(engine);
	}	

	private void checkInstancesConsistency(SimulationEngine engine) throws Exception {
	
	}

	private void checkExecutionDate(SimActivityInstance elem,Set<SimActivityInstance> parsed) {
		for(SimActivityInstance activity : elem.getNext()){
			if(activity.getExecutionDate() != 0){
				parsed.add(activity) ;
				if(((SimActivity) activity.getDefinition()).getJoinType().equals(JoinType.XOR)){
					assertTrue("next elem : "+activity.getDefinition().getName()+" starts @ "+activity.getStartDate()+" and elem starts @ "+elem.getStartDate(),activity.getStartDate() >= elem.getStartDate()); 
				}
				if(activity.hasNext()){
					for(SimActivityInstance nextInstance : activity.getNext()){
						if(!parsed.contains(nextInstance)){
							checkExecutionDate(nextInstance,parsed);
						}
					}
				
				}
			}
		}
	}

	public void testHourScaleExecutionLowDensityProfileWithDelay() throws Exception {

		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 20) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ;
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, 30*SimulationTestUtil.getHoursIntoMilliseconds(24)) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;
		SimulationEngine engine = new SimulationEngine(SimulationTestUtil.createProcessDefinition(),SimulationTestUtil.createLoadProfileWithLowDensity(),SimulationTestUtil.createResources(),executionProperties); 


		engine.start();
		validateExecution(engine);
	}



	public void testHourScaleExecutionMediumDensityProfileNoDelay() throws Exception {

		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 0) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ;
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, 30*SimulationTestUtil.getHoursIntoMilliseconds(24)) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;
		SimulationEngine engine = new SimulationEngine(SimulationTestUtil.createProcessDefinition(),SimulationTestUtil.createLoadProfileWithMediumDensity(),SimulationTestUtil.createResources(),executionProperties); 


		engine.start();
		validateExecution(engine);
	}

	public void testHourScaleProcessExecutionHighDensityProfileNoDelay() throws Exception {

		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 0) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ;
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, 30*SimulationTestUtil.getHoursIntoMilliseconds(24)) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;
		SimulationEngine engine = new SimulationEngine(SimulationTestUtil.createProcessDefinition(),SimulationTestUtil.createLoadProfileWithHighDensity(),SimulationTestUtil.createResources(),executionProperties); 


		engine.start();
		validateExecution(engine);
	}

	public void testHourScaleProcessExecutionVeryHighDensityProfileNoDelay() throws Exception {

		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 0) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ;
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, 30*SimulationTestUtil.getHoursIntoMilliseconds(24)) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;
		SimulationEngine engine = new SimulationEngine(SimulationTestUtil.createProcessDefinition(),SimulationTestUtil.createLoadProfileWithVeryHighDensity(),SimulationTestUtil.createResources(),executionProperties); 


		engine.start();
		validateExecution(engine);
	}

	public void testHourScaleProcessExecutionVeryHighDensityProfileWithDelay() throws Exception {

		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 10) ;
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, SimulationTestUtil.getHoursIntoMilliseconds(24)*30) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;
		SimulationEngine engine = new SimulationEngine(SimulationTestUtil.createProcessDefinition(),SimulationTestUtil.createLoadProfileWithVeryHighDensity(),SimulationTestUtil.createResources(),executionProperties); 


		engine.start();
		validateExecution(engine);
	}

	public void testTodoListSize() throws Exception {
		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 10) ;
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, SimulationTestUtil.getHoursIntoMilliseconds(24)*30) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ;
		SimulationEngine engine = new SimulationEngine(SimulationTestUtil.createProcessDefinition(),SimulationTestUtil.createLoadProfileWithHighDensity(),SimulationTestUtil.createResources(),executionProperties); 
		engine.createTodoList();
		assertEquals(engine.getLoadProfile().getTotalInjectedInstances(), engine.getStore().getStoredProcessInstances(-1).size()) ;
	}

	public void testHourScaleProcessExecutionHighDensityProfileWithDelay() throws Exception {

		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 20) ;
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, SimulationTestUtil.getHoursIntoMilliseconds(24)*30) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;
		SimulationEngine engine = new SimulationEngine(SimulationTestUtil.createProcessDefinition(),SimulationTestUtil.createLoadProfileWithHighDensity(),SimulationTestUtil.createResources(),executionProperties); 
		engine.start();

		validateExecution(engine);
	}



	public void testCreateTodoList() throws Exception{

		List<InjectionPeriod> injections = new ArrayList<InjectionPeriod>();

		Calendar myInstance = GregorianCalendar.getInstance();
		myInstance .set(2010, 6, 1, 0, 0);
		long p1Start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.MONTH, 3) ;
		long p1End = myInstance.getTimeInMillis() ;
		Period period1 = new Period(p1Start, p1End) ;


		injections.add(new InjectionPeriod(period1,10000,RepartitionType.CONSTANT));
		LoadProfile lp = new LoadProfile(SimulationTestUtil.createWorkingWeekCalendar(), injections);

		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 10) ;
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, SimulationTestUtil.getHoursIntoMilliseconds(24)*30) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ;
		SimulationEngine engine = new SimulationEngine(SimulationTestUtil.createProcessDefinition(),lp,SimulationTestUtil.createResources(),executionProperties); 
		engine.createTodoList() ;
		PriorityQueue<RuntimeTask> todoList = new PriorityQueue<RuntimeTask>();
		List<SimProcessInstance> instances = engine.getStore().getStoredProcessInstances(-1 ) ;
		for(SimProcessInstance instance : instances){
			for(SimActivityInstance startActivity : instance.getStartElemInstances()){
				todoList.add(new RuntimeTask(startActivity,instance.getStartDate()));
			}
		}
		Iterator<RuntimeTask> it = todoList.iterator();
		while (it.hasNext()) {
			RuntimeTask runtimeTask = (RuntimeTask) it.next();
			assertTrue("Starting time has unavailable time : "+runtimeTask.getStartDate(), engine.getLoadProfile().getInjectionCalendar().isPlanningAvailable(runtimeTask.getStartDate())); //$NON-NLS-1$
		}
		int totalInstances = 0 ;
		for(InjectionPeriod p : engine.getLoadProfile().getInjectionPeriods()){
			totalInstances = totalInstances + p.getNumberOfInstance() ;
		}
		assertEquals("Missing or too many instances planned",totalInstances, todoList.size()); //$NON-NLS-1$
		//assertTrue("Bad repartition",((RuntimeTask)todoList.toArray()[todoList.size()-1]).getStartDate() <= lp.getInjectionPeriods().get(lp.getInjectionPeriods().size()-1).getPeriod().getEnd()); //$NON-NLS-1$

	}

	public void testMinuteScaleExecutionLowDensityProfileNoDelay() throws Exception {

		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY,0) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ;
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, 30*SimulationTestUtil.getHoursIntoMilliseconds(24)) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;

		Calendar myInstance = GregorianCalendar.getInstance() ;
		List<InjectionPeriod> injections = new ArrayList<InjectionPeriod>();

		myInstance.set(2010, 6, 1, 0, 0);
		long p1Start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.MONTH, 40) ;
		long p1End = myInstance.getTimeInMillis() ;
		Period period1 = new Period(p1Start, p1End) ;


		injections.add(new InjectionPeriod(period1,15000,RepartitionType.CONSTANT));
		LoadProfile lp = new LoadProfile(SimulationTestUtil.createWorkingWeekCalendar(), injections);


		SimulationEngine engine = new SimulationEngine(SimulationTestUtil.createProcessDefinitionMinuteScaled(),lp,SimulationTestUtil.createResources(),executionProperties); 

		engine.start();

		validateExecution(engine);
	}

	public void testHourScaleExecutionLowDensityProfileNoDelayAndDataBased() throws Exception {

		Properties executionProperties = new Properties();
		executionProperties.put(SimulationEngine.MAXIMUM_DELAY_ACTIVITY, 0) ;
		executionProperties.put(SimulationEngine.REPORT_TIMESPAN, SimulationTestUtil.getHoursIntoMilliseconds(24)*30) ;
		executionProperties.put(SimulationEngine.REPORT_WORKSPACE, new File("target").getAbsolutePath()) ;
		executionProperties.put(SimulationEngine.FLUSH_STORE, false) ;
		SimulationEngine engine = new SimulationEngine(SimulationTestUtil.createProcessDefinitionBasedOnData(),SimulationTestUtil.createLoadProfileWithLowDensity(),SimulationTestUtil.createResources(),executionProperties); 

		engine.start();

		validateExecution(engine);
	}

	private void checkWorkingPeriodsNotOverlapsEachOther(SimulationEngine engine) throws Exception {
		List<SimProcessInstance> instances = engine.getStore().getStoredFinishedProcessInstances(1000) ;
	
		

		for(SimProcessInstance instance : instances){
			SimActivityInstance elem = instance.getStartElemInstances().iterator().next() ;
			Set<SimActivityInstance> parsed = new HashSet<SimActivityInstance>() ;
			checkExecutionDate(elem,parsed) ;
	
		}
		
		checkLockDate(instances) ;
		
		for(RuntimeResource r : ResourcePool.getInstance().getResourceInstances().values()){
			for(ResourceInstance ri : r.getInstances()){
				Object[] array = ri.getPlanning().getWorkingPeriods().toArray();
				for(int i = 0 ; i< array.length-1; i++){
					Period wp = (Period) array[i];
					for(int j= i+1 ; j<array.length;j++){
						assertFalse(wp.overlaps(((Period) array[j])));
					}
				}
			}
		}
	}

	private void checkLockDate(List<SimProcessInstance> instances) throws Exception {
		for(SimProcessInstance processInstance : instances){
			Set<SimActivityInstance> parsed = new HashSet<SimActivityInstance>();
			parseProcessInstance(processInstance.getStartElemInstances(), processInstance.getStartElemInstances().iterator().next().getStartDate(),parsed) ;
		}
	}

	private void parseProcessInstance(Set<SimActivityInstance> activityInstances, long previousDate, Set<SimActivityInstance> parsed) {
		for(SimActivityInstance a : activityInstances){
			if(a.getStartDate() != 0){
				parsed.add(a) ;
				if(((SimActivity) a.getDefinition()).getJoinType().equals(JoinType.XOR)){
					assertTrue(a.getDefinition().getName() +" starts @ "+a.getStartDate() + " and previous date is "+ previousDate,a.getStartDate() >= previousDate) ;
				}
				if(a.hasNext()){
					for(SimActivityInstance nextInstance : a.getNext()){
						if(!parsed.contains(nextInstance)){
							parseProcessInstance(Collections.singleton(nextInstance),a.getFinishDate(),parsed) ;
						}
					}
					
				}
			}
		}
	}

}
