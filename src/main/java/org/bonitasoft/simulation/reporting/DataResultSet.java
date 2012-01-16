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

package org.bonitasoft.simulation.reporting;

import java.util.HashMap;
import java.util.Map;

import org.bonitasoft.simulation.model.process.SimBooleanData;
import org.bonitasoft.simulation.model.process.SimData;
import org.bonitasoft.simulation.model.process.SimLiteral;
import org.bonitasoft.simulation.model.process.SimLiteralsData;

/**
 * @author Romain Bioteau
 *
 */
public class DataResultSet {

	private double totalAvgValue ;
	private double totalMedValue ;
	private double totalMinValue = Double.MAX_VALUE ;
	private double totalMaxValue ;


	private Map<String, Double> literalsRepartition ;
	private SimData data;
	private double totalValue;
	private double nbValues;


	public DataResultSet(SimData data){
		this.data = data ;
		
		literalsRepartition = new HashMap<String, Double>();
		if(data instanceof SimBooleanData){
			literalsRepartition.put(Boolean.TRUE.toString(), 0.0);
			literalsRepartition.put(Boolean.FALSE.toString(), 0.0);
		}else if( data instanceof SimLiteralsData){
			for(SimLiteral lit : ((SimLiteralsData) data).getLitterals() ){
				literalsRepartition.put(lit.getLitValue(), 0.0) ;
			}
		}
	}


	/**
	 * @return the totalAvgValue
	 */
	public double getTotalAvgValue() {
		return totalAvgValue;
	}


	/**
	 * @param totalAvgValue the totalAvgValue to set
	 */
	public void setTotalAvgValue(double totalAvgValue) {
		this.totalAvgValue = totalAvgValue;
	}


	/**
	 * @return the totalMedValue
	 */
	public double getTotalMedValue() {
		return totalMedValue;
	}


	/**
	 * @param totalMedValue the totalMedValue to set
	 */
	public void setTotalMedValue(double totalMedValue) {
		this.totalMedValue = totalMedValue;
	}


	/**
	 * @return the totalMinValue
	 */
	public double getTotalMinValue() {
		return totalMinValue;
	}


	/**
	 * @param totalMinValue the totalMinValue to set
	 */
	public void setTotalMinValue(double totalMinValue) {
		this.totalMinValue = totalMinValue;
	}


	/**
	 * @return the totalMaxValue
	 */
	public double getTotalMaxValue() {
		return totalMaxValue;
	}


	/**
	 * @param totalMaxValue the totalMaxValue to set
	 */
	public void setTotalMaxValue(double totalMaxValue) {
		this.totalMaxValue = totalMaxValue;
	}


	/**
	 * @return the literalsRepartition
	 */
	public Map<String, Double> getLiteralsRepartition() {
		return literalsRepartition;
	}


	/**
	 * @param literalsRepartition the literalsRepartition to set
	 */
	public void setLiteralsRepartition(Map<String, Double> literalsRepartition) {
		this.literalsRepartition = literalsRepartition;
	}


	public void addValue(Double value) {
		nbValues++;
		totalValue = totalValue + value ;
		if(value > totalMaxValue){
			totalMaxValue = value ;
		}
		
		if(value < totalMinValue){
			totalMinValue = value ;
		}
	}

	public void computeFinalValues(){
			double avg = (double)totalValue /(double)nbValues ;
			setTotalAvgValue(avg) ;
	}

	public void addOcuurence(String literal){
		Double currentOccurence = literalsRepartition.get(literal) ;
		literalsRepartition.put(literal, currentOccurence+1);
	}


	public void computeFinalRepartion(int totalInstances) {
		for(String lit : literalsRepartition.keySet()){
			Double occurence = literalsRepartition.get(lit) ;
			Double proba = (double) occurence / (double)totalInstances ;
			literalsRepartition.put(lit,proba) ;
		}
		
	}


	public SimData getData() {
		return data;
	}


}
