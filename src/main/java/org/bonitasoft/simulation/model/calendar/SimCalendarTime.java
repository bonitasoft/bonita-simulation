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

/**
 * @author Romain Bioteau
 *
 */
public class SimCalendarTime {

	private int hourOfDay ;
	private int minuteOfHour ;
	
	public SimCalendarTime(int hourOfDay, int minuteOfHour ){
		this.hourOfDay = hourOfDay ;
		this.minuteOfHour = minuteOfHour ;
	}

	public int getHourOfDay() {
		return hourOfDay;
	}
	
	public int getMinuteOfHour() {
		return minuteOfHour;
	}

	public boolean after(int hour, int minute) {
		if(getHourOfDay() > hour ){
			return true;
		}else if(getHourOfDay() == hour && getMinuteOfHour() >= minute){
			return true;
		}else{
			return false ;
		}
	}
	
}
