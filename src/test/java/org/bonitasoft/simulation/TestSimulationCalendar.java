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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

import org.bonitasoft.simulation.engine.ResourceInstanceAvailability;
import org.bonitasoft.simulation.engine.ResourcePool;
import org.bonitasoft.simulation.model.Period;
import org.bonitasoft.simulation.model.TimeUnit;
import org.bonitasoft.simulation.model.calendar.SimCalendar;
import org.bonitasoft.simulation.model.calendar.SimCalendarInstance;
import org.bonitasoft.simulation.model.calendar.SimCalendarPeriod;
import org.bonitasoft.simulation.model.calendar.SimCalendarTime;
import org.bonitasoft.simulation.model.instance.ResourceInstance;
import org.bonitasoft.simulation.model.resource.Resource;

/**
 * @author Romain Bioteau
 *
 */
public class TestSimulationCalendar extends TestCase{


	@SuppressWarnings("deprecation")
	public void testFindAvailablePeriodWhenTaskCanBeDoneImmediatly() throws Exception {
		
		long start = new Date(2010, 06, 18, 9, 0).getTime();
		ResourcePool.getInstance().addResource(SimulationTestUtil.createProcessDefinition(),SimulationTestUtil.createResources().get(0),start,SimulationTestUtil.getHoursIntoMilliseconds(24)) ;
		SimCalendarInstance planning = ResourcePool.getInstance().getResourceInstances(SimulationTestUtil.createResources().get(0)).get(0).getPlanning();
		
		long available = planning.getFirstAvailableDate(start, SimulationTestUtil.getHoursIntoMilliseconds(1), true);
		assertEquals("This calendar should allow the task to be done immediatly", start, available); //$NON-NLS-1$
	}
	
	
	public void testWorkingDuration() throws Exception{
		List<Resource> ressources = SimulationTestUtil.createResources() ;
		Resource r = ressources.get(0) ;
		Calendar cal = GregorianCalendar.getInstance() ;
		cal.set(2010,5,1,8,0,0);
		cal.set(Calendar.MILLISECOND , 0) ;
		long start = cal.getTimeInMillis();
		cal.add(Calendar.DAY_OF_WEEK, 4) ;
		cal.set(Calendar.SECOND, 0) ;
		cal.set(Calendar.MILLISECOND , 0) ;
		long end = cal.getTimeInMillis();
		long duration = r.getPlanning().getWorkingPlanningDuration(start, end) ; 
		assertEquals(4*SimulationTestUtil.getHoursIntoMilliseconds(10), duration) ;
		
		cal.set(2010,5,5,8,0,0);//Saturday
		cal.set(Calendar.MILLISECOND , 0) ;
		
		start = cal.getTimeInMillis();
		
		cal.add(Calendar.DAY_OF_WEEK, 1) ;
		cal.set(Calendar.SECOND, 0) ;
		cal.set(Calendar.MILLISECOND , 0) ;
		end = cal.getTimeInMillis();
		duration = r.getPlanning().getWorkingPlanningDuration(start, end) ; 
		assertEquals(0, duration) ;
		
		cal.set(2010,5,5,4,0,0);
		cal.set(Calendar.MILLISECOND , 0) ;
		start = cal.getTimeInMillis();
		cal.add(Calendar.HOUR_OF_DAY, 1) ;
		cal.set(Calendar.SECOND, 0) ;
		cal.set(Calendar.MILLISECOND , 0) ;
		end = cal.getTimeInMillis();
		duration = r.getPlanning().getWorkingPlanningDuration(start, end) ; 
		assertEquals(0, duration) ;
	
	}
	
	public void testFindAvailablePeriod() throws Exception{
		List<Resource> ressources = SimulationTestUtil.createResources() ;
		Resource r = ressources.get(0) ;
		
		Calendar myInstance = GregorianCalendar.getInstance() ;
		myInstance.set(2010, 6, 1, 9,0);

		
		long start = myInstance.getTimeInMillis() ;

		
		ResourcePool.getInstance().addResource(null,r,start,SimulationTestUtil.getHoursIntoMilliseconds(24));
		ResourceInstance ri = ResourcePool.getInstance().getResourceInstances(ressources.get(0)).get(0);
		
		myInstance.add(Calendar.HOUR, 2) ;
		long end = myInstance.getTimeInMillis() ;
		Period p = new Period(start, end) ;
		ri.getPlanning().addBusyPeriod(p,false) ;
		myInstance.add(Calendar.HOUR, -1);
		long nextTaskToStart = myInstance.getTimeInMillis() ;
		long executionTime = 60000 * 60 * 4 ; // 4 Hours 


		List<ResourceInstanceAvailability> result = ResourcePool.getInstance().findAvailableResource(1,r, nextTaskToStart, executionTime,false);
		Period per = new Period(result.get(0).getTime(),result.get(0).getTime()+executionTime);
		List<Period> periods = result.get(0).getResource().getPlanning().split(per);
		assertEquals(2, periods.size());
		long total = 0 ;
		
		for(Period period : periods){
			total = total + period.getDuration();
		}
		
		assertEquals(60000 * 60 * 4,total) ;
		
		result = ResourcePool.getInstance().findAvailableResource(1,r, nextTaskToStart, executionTime,true);
		per = new Period(result.get(0).getTime(),result.get(0).getTime()+executionTime);
		periods = result.get(0).getResource().getPlanning().split(per);
		assertEquals(1, periods.size());
		total = 0 ;
		
		for(Period period : periods){
			total = total + period.getDuration();
		}
		
		assertEquals(60000 * 60 * 4,total) ;
		

	}
	
	public void testSpliPeriod() throws Exception{
		List<Resource> ressources = SimulationTestUtil.createResources() ;
		Resource r = ressources.get(0) ;
		

		Calendar myInstance = GregorianCalendar.getInstance() ;
		myInstance.set(2010, 6, 1, 9,0);
		myInstance.set(Calendar.MILLISECOND, 0);
		myInstance.set(Calendar.SECOND, 0);
		
		long start = myInstance.getTimeInMillis() ;
		ResourcePool.getInstance().addResource(null,r,start,SimulationTestUtil.getHoursIntoMilliseconds(24));
		
		myInstance.add(Calendar.HOUR, 4) ;
		long end = myInstance.getTimeInMillis() ;
		Period p = new Period(start, end) ; 


		List<ResourceInstance> riList = ResourcePool.getInstance().getResourceInstances(r) ;
		List<Period> periods = riList.get(0).getPlanning().split(p) ;
		long total = 0 ;
		for(Period per : periods){
			total = total + per.getDuration();
		}
		
		assertEquals(60000 * 60 * 4,total) ;
		
		SimCalendar c = new SimCalendar();

		Set<SimCalendarPeriod> workingDay = new HashSet<SimCalendarPeriod>() ; 
		SimCalendarPeriod morning = new SimCalendarPeriod(new SimCalendarTime(0,0),new SimCalendarTime(0,0));
		workingDay.add(morning) ;


		c.addSimCalendarDay(Calendar.MONDAY, new TreeSet<SimCalendarPeriod>());
		c.addSimCalendarDay(Calendar.TUESDAY, new TreeSet<SimCalendarPeriod>());
		c.addSimCalendarDay(Calendar.WEDNESDAY, new TreeSet<SimCalendarPeriod>());
		c.addSimCalendarDay(Calendar.THURSDAY, new TreeSet<SimCalendarPeriod>());
		c.addSimCalendarDay(Calendar.FRIDAY, new TreeSet<SimCalendarPeriod>());
		c.addSimCalendarDay(Calendar.SATURDAY, workingDay);
		c.addSimCalendarDay(Calendar.SUNDAY, workingDay);
		Resource res = new Resource("R&D Team","Developer",5,5,c,"$",TimeUnit.HOUR,0,20);
		ResourcePool.getInstance().addResource(null,res,start,SimulationTestUtil.getHoursIntoMilliseconds(24));
		List<ResourceInstance> riList2 = ResourcePool.getInstance().getResourceInstances(res) ;
		periods = riList2.get(0).getPlanning().split(p) ;
		assertEquals(1,periods.size());
	}
	
	public void testGetFirstAvailableDate() throws Exception{
		List<Resource> ressources = SimulationTestUtil.createResources() ;
		Resource r = ressources.get(0) ;


		Calendar myInstance = GregorianCalendar.getInstance() ;
		myInstance.set(2010, 6, 1, 11,0);
		myInstance.set(Calendar.MILLISECOND, 0);
		myInstance.set(Calendar.SECOND, 0);
		
		long start = myInstance.getTimeInMillis() ;
		
		ResourcePool.getInstance().addResource(null,r,start,SimulationTestUtil.getHoursIntoMilliseconds(24));
		
		myInstance.add(Calendar.HOUR, 4) ;
		long end = myInstance.getTimeInMillis() ;
		Period p = new Period(start, end) ; 

		
		myInstance.add(Calendar.HOUR, -1) ;
		long wStart = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.HOUR, 3) ;
		long wEnd = myInstance.getTimeInMillis() ;
	
		
		List<ResourceInstance> riList = ResourcePool.getInstance().getResourceInstances(r) ;
		Period wp = new Period(wStart,wEnd); 
		riList.get(0).getPlanning().addBusyPeriod(wp,false); 
		
		assertTrue(riList.get(0).getPlanning().isWorkingDuring(wp)) ;
		
		long startDate = riList.get(0).getPlanning().getFirstAvailableDate(start, p.getDuration(), false);
		assertTrue(startDate >= wp.getEnd()) ;
		
		startDate = riList.get(0).getPlanning().getNextPlanningUnavailable(startDate) ;
		assertTrue(riList.get(0).getPlanning().isPlanningAvailable(riList.get(0).getPlanning().getFirstAvailableDate(startDate,10,true))) ;

	}
	
	
	public void testPeriodOverlap() throws Exception{

		Calendar myInstance = GregorianCalendar.getInstance() ;
		myInstance.set(2010, 6, 1, 11,0);
		myInstance.set(Calendar.MILLISECOND, 0);
		myInstance.set(Calendar.SECOND, 0);
		
		long start = myInstance.getTimeInMillis() ;
		
		
		myInstance.add(Calendar.HOUR, 4) ;
		long end = myInstance.getTimeInMillis() ;
		Period p11_15 = new Period(start, end) ; 

		
		myInstance.add(Calendar.HOUR, -5) ;
		start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.HOUR, 2) ;
		end = myInstance.getTimeInMillis() ;
		Period p10_12 = new Period(start, end) ; 
		
		
		myInstance.add(Calendar.HOUR, 2) ;
		start = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.HOUR, 2) ;
		end = myInstance.getTimeInMillis() ;
		Period p14_16 = new Period(start, end) ;
		
	
		start = end ;
		myInstance.add(Calendar.HOUR, 1) ;
		end = myInstance.getTimeInMillis() ;
		Period p16_17 = new Period(start, end) ; 
		
		assertTrue(p11_15.overlaps(p11_15)) ;
		assertTrue(p11_15.overlaps(p10_12)) ;
		assertTrue(p10_12.overlaps(p11_15)) ;
		assertTrue(p14_16.overlaps(p11_15)) ;
		assertTrue(p11_15.overlaps(p14_16)) ;
		
		assertFalse(p10_12.overlaps(p14_16)) ;
		assertFalse(p14_16.overlaps(p10_12)) ;
		
		assertFalse(p16_17.overlaps(p14_16)) ;

	}
	
	public void testNextPlanningAvailableDate() throws Exception{
		List<Resource> ressources = SimulationTestUtil.createResources() ;
		Resource r = ressources.get(0) ;


		Calendar cal = GregorianCalendar.getInstance();
		cal.set(2010, 1, 1, 7,0);
		
		ResourcePool.getInstance().addResource(null,r,cal.getTimeInMillis(),SimulationTestUtil.getHoursIntoMilliseconds(24));
		List<ResourceInstance> riList = ResourcePool.getInstance().getResourceInstances(r) ;
		
		long nextAvailable = riList.get(0).getPlanning().getNextPlanningAvailable(cal.getTimeInMillis());

		assertTrue( riList.get(0).getPlanning().isPlanningAvailable(nextAvailable)) ;
		cal.setTimeInMillis(nextAvailable) ;
		assertTrue(cal.get(Calendar.HOUR_OF_DAY) == 8);
		
		cal.setTimeInMillis(nextAvailable+51000);
		nextAvailable = riList.get(0).getPlanning().getNextPlanningAvailable(cal.getTimeInMillis());
		assertTrue( riList.get(0).getPlanning().isPlanningAvailable(nextAvailable)) ;
		assertEquals(cal.getTimeInMillis(),nextAvailable);
		
		cal.set(2010, 1, 1, 13,10);
		nextAvailable = riList.get(0).getPlanning().getNextPlanningAvailable(cal.getTimeInMillis());
		cal.setTimeInMillis(nextAvailable) ;
		assertTrue( riList.get(0).getPlanning().isPlanningAvailable(nextAvailable)) ;
		assertTrue(cal.get(Calendar.HOUR_OF_DAY) == 13 && cal.get(Calendar.MINUTE) == 30);
	
		cal.set(2010, 1, 1, 20,0);
		nextAvailable = riList.get(0).getPlanning().getNextPlanningAvailable(cal.getTimeInMillis());
		cal.setTimeInMillis(nextAvailable) ;
		assertTrue( riList.get(0).getPlanning().isPlanningAvailable(nextAvailable)) ;
		assertTrue(cal.get(Calendar.DAY_OF_WEEK) == 3 && cal.get(Calendar.HOUR_OF_DAY) == 8);
	
	}
	
	public void testNextPlanningUnavailableDate() throws Exception{
		List<Resource> ressources = SimulationTestUtil.createResources() ;
		Resource r = ressources.get(0) ;
	

		Calendar cal = GregorianCalendar.getInstance();
		cal.set(2010, 1, 1, 7,0);
		
		ResourcePool.getInstance().addResource(null,r,cal.getTimeInMillis(),SimulationTestUtil.getHoursIntoMilliseconds(24));
		List<ResourceInstance> riList = ResourcePool.getInstance().getResourceInstances(r) ;
		
		long nextUnAvailable = riList.get(0).getPlanning().getNextPlanningUnavailable(cal.getTimeInMillis());
		assertFalse( riList.get(0).getPlanning().isPlanningAvailable(nextUnAvailable)) ;
		cal.setTimeInMillis(nextUnAvailable);
		assertTrue(cal.get(Calendar.HOUR_OF_DAY) == 7);
		cal.add(Calendar.HOUR_OF_DAY, 2);
		nextUnAvailable = riList.get(0).getPlanning().getNextPlanningUnavailable(cal.getTimeInMillis());
		
		assertFalse( riList.get(0).getPlanning().isPlanningAvailable(nextUnAvailable)) ;
		cal.setTimeInMillis(nextUnAvailable);
		assertTrue(cal.get(Calendar.HOUR_OF_DAY) == 12);
		
		SimCalendar calendar = new SimCalendar();

		Set<SimCalendarPeriod> workingDay = new HashSet<SimCalendarPeriod>() ; 
		SimCalendarPeriod allDay = new SimCalendarPeriod(new SimCalendarTime(0,0),new SimCalendarTime(0,0));
		workingDay.add(allDay) ;


		calendar.addSimCalendarDay(Calendar.MONDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.TUESDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.WEDNESDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.THURSDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.FRIDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.SATURDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.SUNDAY, workingDay);
		
		Resource res = new Resource("name", "type", 1,1, calendar,"kudos", TimeUnit.DAY,0.0,0.0);
		assertTrue(res.getPlanning().getNextPlanningAvailable(cal.getTimeInMillis()) == cal.getTimeInMillis()) ;
		assertTrue(res.getPlanning().getNextPlanningUnavailable(cal.getTimeInMillis()) == -1);

		
		calendar = new SimCalendar();
		
		Set<SimCalendarPeriod> workingDay2 = new HashSet<SimCalendarPeriod>() ; 
		SimCalendarPeriod notAllDay = new SimCalendarPeriod(new SimCalendarTime(0,0),new SimCalendarTime(2,0));
		workingDay2.add(notAllDay) ;


		calendar.addSimCalendarDay(Calendar.MONDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.TUESDAY, workingDay2);
		calendar.addSimCalendarDay(Calendar.WEDNESDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.THURSDAY, workingDay2);
		calendar.addSimCalendarDay(Calendar.FRIDAY, workingDay);
		calendar.addSimCalendarDay(Calendar.SATURDAY, workingDay2);
		calendar.addSimCalendarDay(Calendar.SUNDAY, workingDay);
		
		res = new Resource("name", "type", 1,1, calendar,"kudos", TimeUnit.DAY,0.0,0.0);
		
		cal.set(2010, 6, 26, 15, 0) ;
		assertTrue(cal.get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY) ;
		assertTrue(res.getPlanning().getNextPlanningAvailable(cal.getTimeInMillis()) == cal.getTimeInMillis()) ;
		System.out.println(new Date(cal.getTimeInMillis()));
		cal.setTimeInMillis(res.getPlanning().getNextPlanningUnavailable(cal.getTimeInMillis()));
		System.out.println(new Date(cal.getTimeInMillis())); 
		assertTrue(cal.get(Calendar.HOUR_OF_DAY) == 2);
	}
	
	
	public void testSimCalendarPeriod() throws Exception{
		SimCalendarPeriod p = new SimCalendarPeriod(new SimCalendarTime(0, 0), new SimCalendarTime(0, 0)) ;
		assertTrue(p.contains(new Date().getTime()));
	}
	
	public void testContigousGetFirstAvailableDate() throws Exception{
		List<Resource> ressources = SimulationTestUtil.createResources() ;
		Resource r = ressources.get(0) ;
	

		Calendar myInstance = GregorianCalendar.getInstance() ;
		myInstance.set(2010, 6, 2, 11,0);
		myInstance.set(Calendar.MILLISECOND, 0);
		myInstance.set(Calendar.SECOND, 0);
		
		long start = myInstance.getTimeInMillis() ;
		ResourcePool.getInstance().addResource(null,r,start,SimulationTestUtil.getHoursIntoMilliseconds(24));
		
		myInstance.add(Calendar.HOUR, 4) ;
		long end = myInstance.getTimeInMillis() ;
		Period p = new Period(start, end) ; 

		
		myInstance.add(Calendar.HOUR, -1) ;
		long wStart = myInstance.getTimeInMillis() ;
		myInstance.add(Calendar.HOUR, 3) ;
		long wEnd = myInstance.getTimeInMillis() ;
	
		
		List<ResourceInstance> riList = ResourcePool.getInstance().getResourceInstances(r) ;
		Period wp = new Period(wStart,wEnd); 
		riList.get(0).getPlanning().addBusyPeriod(wp,false); 
		long startDate = riList.get(0).getPlanning().getFirstAvailableDate(start, p.getDuration(), true);
		assertTrue(riList.get(0).getPlanning().isPlanningAvailable(new Period(startDate,startDate + p.getDuration())));
		
		startDate = riList.get(0).getPlanning().getNextPlanningUnavailable(startDate) ;
		assertTrue(riList.get(0).getPlanning().isPlanningAvailable(riList.get(0).getPlanning().getFirstAvailableDate(startDate,10,true))) ;
		
		
	
	}
	
} 
