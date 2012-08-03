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
 
package org.bonitasoft.simulation.model;

import java.util.Date;


/**
 * @author Romain Bioteau
 *
 */
public class Period implements Comparable<Period> {
	
	private long begin ;
	private long end ;

	public Period(final long begin, final long end){
	  this.begin = begin;
	  this.end = end;
	}

	public void setBegin(long begin) {
		this.begin = begin;
	}

	public long getBegin() {
		return begin;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public long getEnd() {
		return end;
	}
	
	public long getDuration(){
		return getEnd()- getBegin() ;
	}

	public boolean contains(long t) {
		if(t >= getBegin() && t<getEnd()){
			return true ;
		}
		return false;
	}

	public int compareTo(Period p) {
		if(getEnd() < p.getEnd()){
			return 1 ;
		}else if(getEnd() >  p.getEnd()){
			return -1 ;
		}else {
			return 0 ;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Period){
			return getBegin() == ((Period) obj).getBegin() && getEnd() == ((Period) obj).getEnd() ;
		}
		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return (int) (getBegin() + getEnd());
	}

	public boolean overlaps(Period p) {
		if(getBegin() <= p.getBegin() && getEnd() > p.getBegin()){
			return true ;
		}
		
		if(getBegin() >= p.getBegin() && getBegin()< p.getEnd()){
			return true;
		}

		if(getBegin() == p.getBegin() && getEnd() == p.getEnd()){
			return true ;
		}
		return false ;
		
	}
	
	@Override
	public String toString() {
		return "From "+new Date(getBegin())+" to "+ new Date(getEnd());  //$NON-NLS-1$//$NON-NLS-2$
	}
	
}
