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

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Romain Bioteau
 *
 */
public class SimCalendarPeriod {

	private SimCalendarTime start ;
	private SimCalendarTime end ;

	public SimCalendarPeriod(SimCalendarTime start , SimCalendarTime end) {
		this.start = start ;
		this.end = end ;
	}

	public SimCalendarTime getStart() {
		return start;
	}

	public SimCalendarTime getEnd() {
		return end;
	}

	public boolean contains(long instant) {
		Calendar c = GregorianCalendar.getInstance() ;
		c.setTimeInMillis(instant) ;

		int hourOfDay = c.get(Calendar.HOUR_OF_DAY);
		if(hourOfDay >= getStart().getHourOfDay() && (hourOfDay <= getEnd().getHourOfDay() || getEnd().getHourOfDay() == 0)){

			if(getEnd().getHourOfDay() == 0 && getEnd().getMinuteOfHour() == 0){
				return true ;
			}else if(hourOfDay == getStart().getHourOfDay()){
				if(c.get(Calendar.MINUTE) >= getStart().getMinuteOfHour()){
					return true ;
				}
			}else if(hourOfDay == getEnd().getHourOfDay()){
				if(c.get(Calendar.MINUTE) < getEnd().getMinuteOfHour()){
					return true ;
				}
			}else{
				return true ;
			}
		}else{
			return false ;
		}

	return false;

}


}
