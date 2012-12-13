/**
 * Copyright (C) 2010-2012 BonitaSoft S.A.
 * BonitaSoft, 32 rue Gustave Eiffel - 38000 Grenoble
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
package org.bonitasoft.simulation.reporting.jasperreport;

import java.util.Collection;

import net.sf.jasperreports.engine.JRDefaultScriptlet;
import net.sf.jasperreports.engine.JRScriptletException;

/**
 * @author Romain Bioteau
 *
 */
public class HeadingScriptlet extends JRDefaultScriptlet{


	public Boolean addHeading() throws JRScriptletException{
	
		String activityName = (String)this.getFieldValue("ACTIVITY_NAME");
		Collection<HeadingBean> headings = (Collection<HeadingBean>)this.getVariableValue("HeadingsCollection");
		Integer pageindex = (Integer)this.getVariableValue("PAGE_NUMBER");
	
		try{
			headings.add(new org.bonitasoft.simulation.reporting.jasperreport.HeadingBean(activityName, pageindex));
		}
		catch (Exception e){
			e.printStackTrace();
			return false ;
		}
		return true ;
		
	}
}
