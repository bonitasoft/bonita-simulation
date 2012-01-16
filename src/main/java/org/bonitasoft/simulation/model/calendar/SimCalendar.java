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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.bonitasoft.simulation.model.Period;


/**
 * @author Romain Bioteau
 *
 */
public class SimCalendar {

	protected Map<Integer , SimCalendarDay> daysOfWeek ; 
	protected Calendar calendar ;

	public SimCalendar(){
		daysOfWeek = new HashMap<Integer , SimCalendarDay>();
		calendar = GregorianCalendar.getInstance();
	}

	public void addSimCalendarDay(int dayOfWeek , Set<SimCalendarPeriod> workingDay) throws Exception {

		if(daysOfWeek.get(dayOfWeek) != null){
			throw new Exception("Day : "+dayOfWeek+" already defined"); //$NON-NLS-1$ //$NON-NLS-2$
		}

		daysOfWeek.put(dayOfWeek , new SimCalendarDay(dayOfWeek, workingDay)) ;
	}


	public Map<Integer, SimCalendarDay> getDaysOfWeek(){
		return Collections.unmodifiableMap(daysOfWeek) ;
	}

	public long getWorkingPlanningDuration(long from, long to){
		long duration = to-from ;
		long current = from ; 
		while(current < to){
			long next = getNextPlanningAvailable(current) ;

			if(next < to){
				duration = duration - (next-current) ;
				current = next ;
			}else{
				duration = duration - (to-current) ;
				current = to ;
			}
			current = getNextPlanningUnavailable(current);
			if(current == -1){
				current = to ;
			}
		}
		if(duration < 0){
			duration = 0 ;
		}

		return duration ; 
	}


	public long getNextPlanningAvailable(long instant){

		calendar.setTimeInMillis(instant);

		Calendar newCal = GregorianCalendar.getInstance() ;
		newCal.setTimeInMillis(instant) ;


		int initialDay = calendar.get(Calendar.DAY_OF_WEEK) ;
		int i = initialDay ; 
		int nbLoop = 0 ;
		while ( nbLoop < 8){
			SimCalendarDay day = daysOfWeek.get(i); 
			SortedMap<Long,Long> potentialStart = new TreeMap<Long,Long>() ;
			for(SimCalendarPeriod p : day.getWorkingPeriods()){
				if(initialDay == day.getDayOfWeek() && p.contains(instant)){
					return instant ;
				}else if(initialDay == day.getDayOfWeek() && p.getStart().after(calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE))){
					newCal.set(Calendar.HOUR_OF_DAY, p.getStart().getHourOfDay()) ;
					newCal.set(Calendar.MINUTE, p.getStart().getMinuteOfHour()) ;
					newCal.set(Calendar.SECOND,0) ;
					newCal.set(Calendar.MILLISECOND,0) ;
					potentialStart.put(newCal.getTimeInMillis() - instant, newCal.getTimeInMillis()) ;
				}else if(initialDay != day.getDayOfWeek()){
					newCal.set(Calendar.HOUR_OF_DAY, p.getStart().getHourOfDay()) ;
					newCal.set(Calendar.MINUTE, p.getStart().getMinuteOfHour()) ;
					newCal.set(Calendar.SECOND,0) ;
					newCal.set(Calendar.MILLISECOND,0) ;
					potentialStart.put(newCal.getTimeInMillis(), newCal.getTimeInMillis()) ;
				}
			}
			if(!potentialStart.isEmpty()){
				return potentialStart.get(potentialStart.keySet().iterator().next()) ;
			}
			newCal.add(Calendar.DAY_OF_WEEK, 1) ;
			i++;
			i = i % (calendar.getActualMaximum(Calendar.DAY_OF_WEEK)+1 );
			if(i == 0){
				i = 1 ;
			}
			nbLoop ++ ;
		}

		System.err.println("next Available Planning not found..."); //$NON-NLS-1$

		return -1;
	}

	public long getNextPlanningUnavailable(long instant){

		if(!isPlanningAvailable(instant)){
			return instant ;
		}
		
		calendar.setTimeInMillis(instant);
		Calendar newCal = GregorianCalendar.getInstance() ;
		newCal.setTimeInMillis(instant) ;

		int i = calendar.get(Calendar.DAY_OF_WEEK) ;
		int nbLoop = 0 ;
		boolean nextAvailable = false ;
		while ( nbLoop < 8){
			SimCalendarDay day = daysOfWeek.get(i); 
			
			if(day.getWorkingPeriods().isEmpty()){
				newCal.set(Calendar.HOUR_OF_DAY, 0) ;
				newCal.set(Calendar.MINUTE, 0) ;
				newCal.set(Calendar.SECOND,0) ;
				newCal.set(Calendar.MILLISECOND,0) ;
				return newCal.getTimeInMillis() ;
			}
			
			for(SimCalendarPeriod p : day.getWorkingPeriods()){
				if(nextAvailable &&  p.getEnd().getHourOfDay() != 0){
					newCal.set(Calendar.HOUR_OF_DAY, p.getEnd().getHourOfDay()) ;
					newCal.set(Calendar.MINUTE, p.getEnd().getMinuteOfHour()) ;
					newCal.set(Calendar.SECOND,0) ;
					newCal.set(Calendar.MILLISECOND,0) ;
					return newCal.getTimeInMillis() ;
				}else if(p.contains(calendar.getTimeInMillis())){
					if( p.getEnd().getHourOfDay() == 0){
						nextAvailable = true ;
					}else{
						newCal.set(Calendar.HOUR_OF_DAY, p.getEnd().getHourOfDay()) ;
						newCal.set(Calendar.MINUTE, p.getEnd().getMinuteOfHour()) ;
						newCal.set(Calendar.SECOND,0) ;
						newCal.set(Calendar.MILLISECOND,0) ;
						return newCal.getTimeInMillis() ;
					}
				}
			}
			newCal.add(Calendar.DAY_OF_WEEK, 1) ;
			i++;
			i = i % (calendar.getActualMaximum(Calendar.DAY_OF_WEEK)+1 );
			if(i == 0){
				i = 1 ;
			}
			nbLoop ++ ;
		}

		if(getNextPlanningAvailable(instant) == instant){
			instant = -1 ;
		}
		
		return instant ;
	}


	public boolean isPlanningAvailable(long date){
		calendar.setTimeInMillis(date);
		SimCalendarDay day = daysOfWeek.get(calendar.get(Calendar.DAY_OF_WEEK)) ;

		for(SimCalendarPeriod p : day.getWorkingPeriods()){
			if(p.contains(date)){
				return true ;
			}
		}
		return	false ;
	}


	public Set<Long> getAvailablePlanningDurations() {
		Set<Long> result = new TreeSet<Long>();
		calendar.set(2010, 1, 1, 0, 0, 0) ;
		long current = calendar.getTimeInMillis() ; 
		calendar.set(2010, 1, 8, 0, 0, 0) ;
		long to =  calendar.getTimeInMillis() ; 
		while(current < to){
			if(!isPlanningAvailable(current)){
				long next = getNextPlanningAvailable(current) ;
				
				if(next < to){
					current = next ;
				}else{
					current = to ;
				}

			}
			long nextUnavailable = getNextPlanningUnavailable(current) ;
			if(nextUnavailable != -1){
				result.add(nextUnavailable-current) ;
				current = nextUnavailable;
			}else{
				result.add(to-current) ;
				current = to;
			}
			
		}


		return result;
	}


	public boolean isPlanningAvailable(Period period) {

		long periodDuration = period.getDuration() ;
		long start = period.getBegin() ;
		long end = period.getEnd() ;

		long workingDuration = getWorkingPlanningDuration(start, end) ;

		return workingDuration == periodDuration ;
	}

	public List<Period> split(Period period) throws Exception {
		List<Period> results = new ArrayList<Period>();
		long currentDuration = 0 ;
		long totalDuration = period.getDuration() ;
		long start = period.getBegin() ;
		long currentDate = start ;
		if(period.getDuration() == 0  || getNextPlanningUnavailable(new Date().getTime()) == -1){
			return Collections.singletonList(period) ;
		}
		while (currentDuration < totalDuration){
			Period p = new Period(0,0);
			if (!isPlanningAvailable(currentDate) && currentDuration != totalDuration){
				currentDate = getNextPlanningAvailable(currentDate) ;
			}

			if(isPlanningAvailable(currentDate)){
				p.setBegin(currentDate);
				if (currentDuration < totalDuration) {
					long tmp = currentDate ;
					tmp = getNextPlanningUnavailable(tmp) ;
					if((currentDuration+(tmp-currentDate) > totalDuration)){
						currentDate = currentDate+(totalDuration-currentDuration) ;
						currentDuration = totalDuration ;
					}else{
						currentDuration = currentDuration + tmp-currentDate ; 
						currentDate = tmp ;
					}
				}
			} else {
				throw new Exception("Split is inconsistent for Period : "+period+" because "+new Date(currentDate)+" is not available in the planning") ; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}


			p.setEnd(currentDate);
			results.add(p);
		}
		return results;	
	}


	public long getWorkingDurationEndDate(long start, long workedDuration) {
		long currentDuration = 0 ;
		long newStart = getNextPlanningAvailable(start) ;

		long end = newStart+workedDuration ;

		currentDuration = getWorkingPlanningDuration(newStart, end) ;
		while(currentDuration != workedDuration){
			long missingDuration = workedDuration - currentDuration ;
			end = end + missingDuration ;
			currentDuration = getWorkingPlanningDuration(newStart, end) ;
		}

		return getNextPlanningAvailable(end);
	}

}
