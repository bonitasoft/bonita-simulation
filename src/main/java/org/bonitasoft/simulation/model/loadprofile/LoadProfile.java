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
 
package org.bonitasoft.simulation.model.loadprofile;

import java.util.Collections;
import java.util.List;

import org.bonitasoft.simulation.model.calendar.SimCalendar;

/**
 * @author Romain Bioteau
 *
 */
public class LoadProfile {

	private List<InjectionPeriod> injectionPeriods ;
	private SimCalendar injectionCalendar ;
	
	
	public LoadProfile(SimCalendar injectionCalendar, List<InjectionPeriod> injectionPeriods){
	  this.injectionPeriods = injectionPeriods;
	  this.injectionCalendar = injectionCalendar;
	}

	public List<InjectionPeriod> getInjectionPeriods() {
		return Collections.unmodifiableList(injectionPeriods);
	}
	
	public void addInjectionPeriod(InjectionPeriod period){
		injectionPeriods.add(period);
	}

	public SimCalendar getInjectionCalendar() {
		return injectionCalendar;
	}

	public int getTotalInjectedInstances() {
		int total = 0 ;
		for(InjectionPeriod p : injectionPeriods){
			total = total + p.getNumberOfInstance() ;
		}
		return total;
	}

	public long getTotalDuration() {
		long start = Long.MAX_VALUE ;
		long end = 0 ;
		for(InjectionPeriod p : injectionPeriods){
			if( p.getPeriod().getBegin() < start){
				start = p.getPeriod().getBegin() ;
			}
			if(p.getPeriod().getEnd() > end){
				end = p.getPeriod().getEnd() ;
			}
			
		}
		return end - start;
	}

	
}
