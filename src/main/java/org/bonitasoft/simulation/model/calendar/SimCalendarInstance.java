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

package org.bonitasoft.simulation.model.calendar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bonitasoft.simulation.engine.SimulationEngine;
import org.bonitasoft.simulation.model.Period;
import org.bonitasoft.simulation.model.process.ResourceAssignement;
import org.bonitasoft.simulation.model.process.SimActivity;
import org.bonitasoft.simulation.model.process.SimProcess;
import org.bonitasoft.simulation.model.process.SimTransition;


/**
 * @author Romain Bioteau
 *
 */
public class SimCalendarInstance extends SimCalendar {

	private SortedSet<Period> busyPeriods;
	//key=executionTime, value=startTime of the free period
	private Map<Long, Long> contiguousIndexes;
	private Map<Long, Long> allIndexes;
	private SimProcess simProc;
	private String resourceName;

	public SimCalendarInstance(final long startTime,SimProcess process,String resourceName, Map<Integer, SimCalendarDay> daysOfWeek) {
		super();
		busyPeriods = new TreeSet<Period>();
		this.simProc = process ;
		this.resourceName = resourceName ;
		this.daysOfWeek = daysOfWeek ;
	}

	private void initIndexes() throws Exception {
		if (simProc != null && contiguousIndexes == null) {

			SortedSet<Long> durations = new TreeSet<Long>();
			List<SimActivity> parsed = new ArrayList<SimActivity>();
			findAllIndexDuration(simProc.getStartElements(),parsed,durations );

			contiguousIndexes = new TreeMap<Long, Long>();
			allIndexes = new TreeMap<Long, Long>();
			for(Long duration : durations){
				if(isContigousDurationPossible(duration)){
					contiguousIndexes.put(duration, getFirstAvailableDate(SimulationEngine.currentTime, duration, true));
				}
				allIndexes.put(duration, getFirstAvailableDate(SimulationEngine.currentTime, duration, false));
			}
		}
	}

	private boolean isContigousDurationPossible(Long duration) {
		if(getNextPlanningUnavailable(new Date().getTime()) == -1){
			return true ;
		}
		Set<Long> durations = getAvailablePlanningDurations() ;
		for(Long l : durations){
			if(l >= duration){
				return true ;
			}
		}
		return false;
	}



	private void findAllIndexDuration(Set<SimActivity> elements,List<SimActivity> parsed,SortedSet<Long> durations) {
		for(SimActivity a : elements){
			if(!parsed.contains(a)){
				for(ResourceAssignement ra : a.getAssignedResources()){
					if(ra.getResource().getName().equals(resourceName)){
						if(ra.getDuration() > 0){
							durations.add(ra.getDuration()) ;
						}
					}
				}
				parsed.add(a) ;
				for(SimTransition t : a.getOutgoingTransitions()){
					findAllIndexDuration(Collections.singleton(t.getTarget()),parsed, durations);
				}
			}
		}
	}

	public SortedSet<Period> getWorkingPeriods(){
		return Collections.unmodifiableSortedSet(busyPeriods) ;
	}

	public void addBusyPeriod(final Period busyPeriod, boolean flushOldPeriod) throws Exception {
		if(!busyPeriods.add(busyPeriod)){
			throw new Exception("Impossible to add period :"+busyPeriod); //$NON-NLS-1$
		}
		updateIndexes(busyPeriod, true);
		updateIndexes(busyPeriod , false);
		if(flushOldPeriod){
			flushBusyPeriods() ;
		}
	}

	private void flushBusyPeriods() {
		SortedSet<Period> wp = busyPeriods.tailSet(new Period(SimulationEngine.currentTime, SimulationEngine.currentTime));
		busyPeriods.removeAll(new TreeSet<Period>(wp));
	}

	private void updateIndexes(final Period busyPeriod, final boolean contiguous) throws Exception {
		if(simProc != null){
			initIndexes();
			Map<Long, Long> indexes = allIndexes;
			if (contiguous) {
				indexes = contiguousIndexes;
			}

			final Map<Long, Long> newIndexes = new HashMap<Long, Long>();

			for (Map.Entry<Long, Long> index : indexes.entrySet()) {
				final Long indexPeriodDuration = index.getKey();
				final Long indexTime = index.getValue();
				final Period indexPeriod = new Period(indexTime, indexTime + indexPeriodDuration);
				long newIndexTime = indexTime;

				if (busyPeriod.overlaps(indexPeriod)) {
					//calculate new index position from busyPeriod end
					newIndexTime = getFirstAvailableDate(busyPeriod.getEnd(), indexPeriodDuration, contiguous); 
				} else if (indexTime < SimulationEngine.currentTime) {
					newIndexTime = getFirstAvailableDate(SimulationEngine.currentTime, indexPeriodDuration, contiguous);
				}
				newIndexes.put(indexPeriodDuration, newIndexTime);
			}

			if (contiguous) {
				contiguousIndexes = newIndexes;
			} else {
				allIndexes = newIndexes; 
			}
		}
	}

	private long getIndexTime(final long executionTime, final boolean contiguous) throws Exception {
		if(simProc != null){
			initIndexes();
			Map<Long, Long> indexes = allIndexes;
			if (contiguous) {
				indexes = contiguousIndexes;
			}
			if( indexes.get(executionTime) != null){
				return indexes.get(executionTime) ;
			}
		}
		return SimulationEngine.currentTime ;
	}


	public long getNotWorkingDate(long current) {

		for(Period p : busyPeriods){
			if(p.contains(current)){
				current = p.getEnd() ;
			}
		}

		if(!isPlanningAvailable(current)){
			current = getNextPlanningAvailable(current);
		}
		return current ;

	}

	public boolean isWorkingDuring(Period period){
		for (Period wp : busyPeriods) {
			if (wp.overlaps(period)) {
				return true ;
			}
		}
		return false ;
	}


	/**
	 * Check resource availability regarding its working charge and its planning
	 * @param period
	 * @return
	 */
	public boolean isAvailable(Period period) {
		return !isWorkingDuring(period)&& isPlanningAvailable(period); 
	}


	public long getFirstAvailableDate(long start, long executionTime, boolean contigous) throws Exception {
		long current = start ;
		long index = getIndexTime(executionTime, contigous);
		current = getNextPlanningAvailable(current);
		Period p = new Period(current, current + executionTime);

		if (index >= start) {
			current = index;
		} else if (isWorkingDuring(p)) {
			current = getNotWorkingPeriod(p) ;
		}

		if (!contigous) {
			boolean available = false ;
			while (!SimulationEngine.isStopped && !available){//CHECK PERIOD AVAILABILITY
				p = new Period(current, current + executionTime);
				List<Period> periods = split(p) ;
				available = true ;
				for (Period per : periods){//CHECK FOR EACH SPLIITED PERIODS IF THERE ARE VALID (NOT WORKING)
					if (isWorkingDuring(per)){
						available = false ;
						//AS THE PERIOD IS NOT AVAILABLE, UPDATE CURRENT DATE TO A FREE ONE AND RE-LOOP
						current = getNotWorkingPeriod(per);
						break;
					}
				}
			}
		} else {
			boolean available = false ;
			while(!SimulationEngine.isStopped && !available){//CHECK PERIOD AVAILABILITY
				p = new Period(current, current+executionTime);
				available = true ;
				if(isWorkingDuring(p) ){
					available = false ;
					current = getNotWorkingPeriod(p);
					p = new Period(current, current+executionTime);
				}
				
				if(split(p).size() != 1){
					available = false ;
					current = getNextPlanningUnavailable(p.getBegin()) ;
					current = getNextPlanningAvailable(current) ;
				}
			}
		}
		
		return current ;
	}

	public long getNotWorkingPeriod(Period currentPeriod) {

		long newStart = currentPeriod.getBegin() ;
		long last = 0 ;
		for (Period p : busyPeriods){
			if(currentPeriod.overlaps(p)){
				if(last < p.getEnd()){
					last  = p.getEnd() ;
				}
			}
		}
	
		if (last != 0 ){
			newStart = last ;
		}

		if (!isPlanningAvailable(newStart)){
			newStart = getNextPlanningAvailable(newStart);
		}
		return newStart ;
	}

}
