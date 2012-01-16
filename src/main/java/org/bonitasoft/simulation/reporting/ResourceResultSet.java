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

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bonitasoft.simulation.engine.RuntimeResource;
import org.bonitasoft.simulation.model.resource.Resource;

/**
 * @author Romain Bioteau
 *
 */
public class ResourceResultSet {

	private double instanceCost ;
	private double totalCost ;
	private double totalAvgCost ;
	private double totalMedCost ;
	private double totalMinCost = Double.MAX_VALUE;
	private double totalMaxCost ;
	private double intermediateCost ;
	private double intermediateAvgCost ;
	private double intermediateMedCost ;
	private double intermediateMinCost = Double.MAX_VALUE;
	private double intermediateMaxCost ;
	private SortedMap<Long, Double> avgCost ;
	private SortedMap<Long, Double> maxCost ;
	private SortedMap<Long, Double> medCost ;
	private SortedMap<Long, Double> minCost ;

	private int instanceConsumption ;
	private int totalConsumption ;
	private float totalAvgConsumption  ;
	private int totalMedConsumption  ;
	private int totalMinConsumption = Integer.MAX_VALUE ;
	private int totalMaxConsumption  ;
	private int intermediateConsumption ;
	private float intermediateAvgConsumption  ;
	private int intermediateMedConsumption  ;
	private int intermediateMinConsumption = Integer.MAX_VALUE ;
	private int intermediateMaxConsumption  ;
	private SortedMap<Long, Float> avgConsumption  ;
	private SortedMap<Long, Integer> maxConsumption  ;
	private SortedMap<Long, Integer> medConsumption  ;
	private SortedMap<Long, Integer> minConsumption  ;

	private long instanceUseDuration ;
	private long totalUseDuration  ; 
	private double totalUse ;
	private double totalAvgUse ;
	private double totalMedUse ;
	private double totalMinUse = Double.MAX_VALUE;
	private double totalMaxUse ;
	private long intermediateUseDuration  ; 
	private double intermediateUse ;
	private double intermediateAvgUse ;
	private double intermediateMedUse ;
	private double intermediateMinUse = Double.MAX_VALUE;
	private double intermediateMaxUse ;
	private SortedMap<Long, Double> avgUse ;
	private SortedMap<Long, Double> maxUse ;
	private SortedMap<Long, Double> medUse ;
	private SortedMap<Long, Double> minUse ;

	private SortedMap<Long, Long> waitedTime ;
	
	private Resource resource;



	private RuntimeResource runtimeResource;

	private int totalConsumptionSize;
	
	private int intervalCostSize;	
	private int totalCostSize;

	private int totalUseSize;
	
	
	public ResourceResultSet(RuntimeResource runtimeResource){
		this.resource = runtimeResource.getResource();
	

		avgCost = new TreeMap<Long,Double>() ;
		maxCost = new TreeMap<Long,Double>() ;
		medCost = new TreeMap<Long,Double>() ;
		minCost = new TreeMap<Long,Double>() ;

		avgConsumption = new TreeMap<Long,Float>() ;
		maxConsumption = new TreeMap<Long,Integer>() ;
		medConsumption = new TreeMap<Long,Integer>() ;
		minConsumption = new TreeMap<Long,Integer>() ;

		avgUse = new TreeMap<Long,Double>() ;
		maxUse = new TreeMap<Long,Double>() ;
		medUse = new TreeMap<Long,Double>() ;
		minUse = new TreeMap<Long,Double>() ;

		waitedTime = new TreeMap<Long, Long>() ;
		
		this.runtimeResource = runtimeResource ;
	}


	/**
	 * @return the totalCost
	 */
	public double getTotalCost() {
		return totalCost;
	}


	/**
	 * @param totalCost the totalCost to set
	 */
	public void setTotalCost(double totalCost) {
		this.totalCost = totalCost;
	}


	/**
	 * @return the totalAvgCost
	 */
	public double getTotalAvgCost() {
		return totalAvgCost;
	}


	/**
	 * @param totalAvgCost the totalAvgCost to set
	 */
	public void setTotalAvgCost(double totalAvgCost) {
		this.totalAvgCost = totalAvgCost;
	}


	/**
	 * @return the totalMedCost
	 */
	public double getTotalMedCost() {
		return totalMedCost;
	}


	/**
	 * @param totalMedCost the totalMedCost to set
	 */
	public void setTotalMedCost(double totalMedCost) {
		this.totalMedCost = totalMedCost;
	}


	/**
	 * @return the totalMinCost
	 */
	public double getTotalMinCost() {
		return totalMinCost;
	}


	/**
	 * @param totalMinCost the totalMinCost to set
	 */
	public void setTotalMinCost(double totalMinCost) {
		this.totalMinCost = totalMinCost;
	}


	/**
	 * @return the totalMaxCost
	 */
	public double getTotalMaxCost() {
		return totalMaxCost;
	}


	/**
	 * @param totalMaxCost the totalMaxCost to set
	 */
	public void setTotalMaxCost(double totalMaxCost) {
		this.totalMaxCost = totalMaxCost;
	}


	/**
	 * @return the avgCost
	 */
	public SortedMap<Long, Double> getAvgCost() {
		return avgCost;
	}


	/**
	 * @param avgCost the avgCost to set
	 */
	public void setAvgCost(SortedMap<Long, Double> avgCost) {
		this.avgCost = avgCost;
	}


	/**
	 * @return the maxCost
	 */
	public SortedMap<Long, Double> getMaxCost() {
		return maxCost;
	}


	/**
	 * @param maxCost the maxCost to set
	 */
	public void setMaxCost(SortedMap<Long, Double> maxCost) {
		this.maxCost = maxCost;
	}


	/**
	 * @return the medCost
	 */
	public SortedMap<Long, Double> getMedCost() {
		return medCost;
	}


	/**
	 * @param medCost the medCost to set
	 */
	public void setMedCost(SortedMap<Long, Double> medCost) {
		this.medCost = medCost;
	}


	/**
	 * @return the minCost
	 */
	public SortedMap<Long, Double> getMinCost() {
		return minCost;
	}


	/**
	 * @param minCost the minCost to set
	 */
	public void setMinCost(SortedMap<Long, Double> minCost) {
		this.minCost = minCost;
	}


	/**
	 * @return the totalConsumption
	 */
	public int getTotalConsumption() {
		return totalConsumption;
	}


	/**
	 * @param totalConsumption the totalConsumption to set
	 */
	public void setTotalConsumption(int totalConsumption) {
		this.totalConsumption = totalConsumption;
	}


	/**
	 * @return the totalAvgConsumption
	 */
	public float getTotalAvgConsumption() {
		return totalAvgConsumption;
	}


	/**
	 * @param totalAvgConsumption the totalAvgConsumption to set
	 */
	public void setTotalAvgConsumption(float totalAvgConsumption) {
		this.totalAvgConsumption = totalAvgConsumption;
	}


	/**
	 * @return the totalMedConsumption
	 */
	public int getTotalMedConsumption() {
		return totalMedConsumption;
	}


	/**
	 * @param totalMedConsumption the totalMedConsumption to set
	 */
	public void setTotalMedConsumption(int totalMedConsumption) {
		this.totalMedConsumption = totalMedConsumption;
	}


	/**
	 * @return the totalMinConsumption
	 */
	public int getTotalMinConsumption() {
		return totalMinConsumption;
	}


	/**
	 * @param totalMinConsumption the totalMinConsumption to set
	 */
	public void setTotalMinConsumption(int totalMinConsumption) {
		this.totalMinConsumption = totalMinConsumption;
	}


	/**
	 * @return the totalMaxConsumption
	 */
	public int getTotalMaxConsumption() {
		return totalMaxConsumption;
	}


	/**
	 * @param totalMaxConsumption the totalMaxConsumption to set
	 */
	public void setTotalMaxConsumption(int totalMaxConsumption) {
		this.totalMaxConsumption = totalMaxConsumption;
	}


	/**
	 * @return the avgConsumption
	 */
	public SortedMap<Long, Float> getAvgConsumption() {
		return avgConsumption;
	}


	/**
	 * @param avgConsumption the avgConsumption to set
	 */
	public void setAvgConsumption(SortedMap<Long, Float> avgConsumption) {
		this.avgConsumption = avgConsumption;
	}


	/**
	 * @return the maxConsumption
	 */
	public SortedMap<Long, Integer> getMaxConsumption() {
		return maxConsumption;
	}


	/**
	 * @param maxConsumption the maxConsumption to set
	 */
	public void setMaxConsumption(SortedMap<Long, Integer> maxConsumption) {
		this.maxConsumption = maxConsumption;
	}


	/**
	 * @return the medConsumption
	 */
	public SortedMap<Long, Integer> getMedConsumption() {
		return medConsumption;
	}


	/**
	 * @param medConsumption the medConsumption to set
	 */
	public void setMedConsumption(SortedMap<Long, Integer> medConsumption) {
		this.medConsumption = medConsumption;
	}


	/**
	 * @return the minConsumption
	 */
	public SortedMap<Long, Integer> getMinConsumption() {
		return minConsumption;
	}


	/**
	 * @param minConsumption the minConsumption to set
	 */
	public void setMinConsumption(SortedMap<Long, Integer> minConsumption) {
		this.minConsumption = minConsumption;
	}


	/**
	 * @return the totalUse
	 */
	public double getTotalUse() {
		return totalUse;
	}


	/**
	 * @param totalUse the totalUse to set
	 */
	public void setTotalUse(double totalUse) {
		this.totalUse = totalUse;
	}


	/**
	 * @return the totalAvgUse
	 */
	public double getTotalAvgUse() {
		return totalAvgUse;
	}


	/**
	 * @param totalAvgUse the totalAvgUse to set
	 */
	public void setTotalAvgUse(double totalAvgUse) {
		this.totalAvgUse = totalAvgUse;
	}


	/**
	 * @return the totalMedUse
	 */
	public double getTotalMedUse() {
		return totalMedUse;
	}


	/**
	 * @param totalMedUse the totalMedUse to set
	 */
	public void setTotalMedUse(double totalMedUse) {
		this.totalMedUse = totalMedUse;
	}


	/**
	 * @return the totalMinUse
	 */
	public double getTotalMinUse() {
		return totalMinUse;
	}


	/**
	 * @param totalMinUse the totalMinUse to set
	 */
	public void setTotalMinUse(double totalMinUse) {
		this.totalMinUse = totalMinUse;
	}


	/**
	 * @return the totalMaxUse
	 */
	public double getTotalMaxUse() {
		return totalMaxUse;
	}


	/**
	 * @param totalMaxUse the totalMaxUse to set
	 */
	public void setTotalMaxUse(double totalMaxUse) {
		this.totalMaxUse = totalMaxUse;
	}


	/**
	 * @return the avgUse
	 */
	public SortedMap<Long, Double> getAvgUse() {
		return avgUse;
	}


	/**
	 * @param avgUse the avgUse to set
	 */
	public void setAvgUse(SortedMap<Long, Double> avgUse) {
		this.avgUse = avgUse;
	}


	/**
	 * @return the maxUse
	 */
	public SortedMap<Long, Double> getMaxUse() {
		return maxUse;
	}


	/**
	 * @param maxUse the maxUse to set
	 */
	public void setMaxUse(SortedMap<Long, Double> maxUse) {
		this.maxUse = maxUse;
	}


	/**
	 * @return the medUse
	 */
	public SortedMap<Long, Double> getMedUse() {
		return medUse;
	}


	/**
	 * @param medUse the medUse to set
	 */
	public void setMedUse(SortedMap<Long, Double> medUse) {
		this.medUse = medUse;
	}


	/**
	 * @return the minUse
	 */
	public SortedMap<Long, Double> getMinUse() {
		return minUse;
	}


	/**
	 * @param minUse the minUse to set
	 */
	public void setMinUse(SortedMap<Long, Double> minUse) {
		this.minUse = minUse;
	}


	/**
	 * @return the instanceCost
	 */
	public double getInstanceCost() {
		return instanceCost;
	}


	/**
	 * @param instanceCost the instanceCost to set
	 */
	public void setInstanceCost(double instanceCost) {
		this.instanceCost = instanceCost;
	}


	/**
	 * @return the instanceConsumption
	 */
	public int getInstanceConsumption() {
		return instanceConsumption;
	}


	/**
	 * @param instanceConsumption the instanceConsumption to set
	 */
	public void setInstanceConsumption(int instanceConsumption) {
		this.instanceConsumption = instanceConsumption;
	}


	public void updateResourceConsumption() {
		int instanceConsumption = getInstanceConsumption() ;

		totalConsumptionSize ++ ;

		int total = getTotalConsumption() ;
		total = total + instanceConsumption ;
		setTotalConsumption(total) ;


		int max = getTotalMaxConsumption() ;

		if(max < instanceConsumption){
			setTotalMaxConsumption(instanceConsumption);
		}

		int min = getTotalMinConsumption() ;
		
	
		if(min > instanceConsumption){
			setTotalMinConsumption(instanceConsumption);
		}
	
		setInstanceConsumption(0) ;
	}


	public void updateResourceCost() {
		
		double instanceCost = getInstanceCost() ;

	
		intervalCostSize ++ ;
		totalCostSize ++;
		
		double total = getTotalCost() ;
		total = total + instanceCost ;
		setTotalCost(total) ;
		
		double intermediate = getIntermediateCost() ;
		intermediate = intermediate + instanceCost ;
		setIntermediateCost(intermediate) ;
		
		double max = getTotalMaxCost() ;
		if(max < instanceCost){
			setTotalMaxCost(instanceCost);
		}

		double min = getTotalMinCost() ;
		if(min > instanceCost){
			setTotalMinCost(instanceCost);
		}

		double avg = (double) getIntermediateCost() / (double) intervalCostSize  ;
		setIntermediateAvgCost(avg) ;
		
		setInstanceCost(0) ;

	}


	public void updateResourceUse(long instanceDuration) {
		
		long instanceUseDuration = getInstanceUseDuration() ;
	
		double instanceUse = 0 ;
		if(instanceDuration > 0){
			instanceUse = (double) instanceUseDuration / (double) instanceDuration ;
		}
		totalUseSize ++ ;

		long total = getTotalUseDuration() ;
		total = total + instanceUseDuration ;
		setTotalUseDuration(total) ;
		
		double totalUse = getTotalUse() ;
		totalUse = totalUse + instanceUse ;
		setTotalUse(totalUse) ;
		
		double intermediateUse = getIntermediateUse() ;
		intermediateUse = intermediateUse + instanceUse ;
		setIntermediateUse(intermediateUse) ;
		
		long intermediate = getIntermediateUseDuration() ;
		intermediate = intermediate + instanceUseDuration ;
		setIntermediateUseDuration(intermediate) ;

		double max = getTotalMaxUse() ;
	
		if(max < instanceUse){
			setTotalMaxUse(instanceUse);
		}

		double min = getTotalMinUse() ;

		if(min > instanceUse){
			setTotalMinUse(instanceUse);
		}

		setInstanceUseDuration(0) ;

	}


	public void reset() {
		setIntermediateAvgConsumption(0) ;
		setIntermediateMinConsumption(Integer.MAX_VALUE) ;
		setIntermediateMaxConsumption(0) ;
		setIntermediateMedConsumption(0) ;
		setIntermediateConsumption(0);
		
		setIntermediateAvgCost(0) ;
		setIntermediateMinCost(Double.MAX_VALUE) ;
		setIntermediateMaxCost(0) ;
		setIntermediateMedCost(0) ;
		setIntermediateCost(0);
		
		setIntermediateAvgUse(0) ;
		setIntermediateMinUse(Double.MAX_VALUE) ;
		setIntermediateMaxUse(0) ;
		setIntermediateMedUse(0) ;
		setIntermediateUseDuration(0);
		setIntermediateUse(0);
		
		intervalCostSize = 0 ;
		//intervalUseSize = 0 ;

	}


	public void computeFinalResourceConsumption() {

		
		float avg = (float) getTotalConsumption() / (float)totalConsumptionSize;
		setTotalAvgConsumption(avg) ;
		
		
		avgConsumption = runtimeResource.getAverageWorkingInstance() ;
		minConsumption = runtimeResource.getMinimumWorkingInstance() ;
		maxConsumption = runtimeResource.getMaximumWorkingInstance();
		
	}

	public void computeFinalResourceCost() {
	
		double avg = (double) getTotalCost() / (double) totalCostSize  ;
		setTotalAvgCost(avg) ;
	}


	public void computeFinalResourceUse(long totalDuration) {
	
		if(totalUseSize ==0){
			setTotalAvgUse(0) ;
		}else{
			double avg = (double) getTotalUse() / totalUseSize ;
			setTotalAvgUse(avg) ;
		}
		
		if(totalDuration == 0){
			setTotalUse(0);
		}else{
			double totalUse = (double) getTotalUseDuration() / totalDuration;
			setTotalUse(totalUse);
		}
	}


	public void setResource(Resource resource) {
		this.resource = resource;
	}


	public Resource getResource() {
		return resource;
	}


	public void setInstanceUseDuration(long instanceUseDuration) {
		this.instanceUseDuration = instanceUseDuration;
	}


	public long getInstanceUseDuration() {
		return instanceUseDuration;
	}


	public void setTotalUseDuration(long totalUseDuration) {
		this.totalUseDuration = totalUseDuration;
	}


	public long getTotalUseDuration() {
		return totalUseDuration;
	}


	public void setIntermediateConsumption(int intermediateConsumption) {
		this.intermediateConsumption = intermediateConsumption;
	}


	public int getIntermediateConsumption() {
		return intermediateConsumption;
	}


	public void setIntermediateAvgConsumption(float intermediateAvgConsumption) {
		this.intermediateAvgConsumption = intermediateAvgConsumption;
	}


	public float getIntermediateAvgConsumption() {
		return intermediateAvgConsumption;
	}


	public void setIntermediateMedConsumption(int intermediateMedConsumption) {
		this.intermediateMedConsumption = intermediateMedConsumption;
	}


	public int getIntermediateMedConsumption() {
		return intermediateMedConsumption;
	}


	public void setIntermediateMinConsumption(int intermediateMinConsumption) {
		this.intermediateMinConsumption = intermediateMinConsumption;
	}


	public int getIntermediateMinConsumption() {
		return intermediateMinConsumption;
	}


	public void setIntermediateMaxConsumption(int intermediateMaxConsumption) {
		this.intermediateMaxConsumption = intermediateMaxConsumption;
	}


	public int getIntermediateMaxConsumption() {
		return intermediateMaxConsumption;
	}


	public void setIntermediateUseDuration(long intermediateUseDuration) {
		this.intermediateUseDuration = intermediateUseDuration;
	}


	public long getIntermediateUseDuration() {
		return intermediateUseDuration;
	}


	public void setIntermediateAvgUse(double intermediateAvgUse) {
		this.intermediateAvgUse = intermediateAvgUse;
	}


	public double getIntermediateAvgUse() {
		return intermediateAvgUse;
	}


	public void setIntermediateMedUse(double intermediateMedUse) {
		this.intermediateMedUse = intermediateMedUse;
	}


	public double getIntermediateMedUse() {
		return intermediateMedUse;
	}


	public void setIntermediateUse(double intermediateUse) {
		this.intermediateUse = intermediateUse;
	}


	public double getIntermediateUse() {
		return intermediateUse;
	}


	public void setIntermediateMinUse(double intermediateMinUse) {
		this.intermediateMinUse = intermediateMinUse;
	}


	public double getIntermediateMinUse() {
		return intermediateMinUse;
	}


	public void setIntermediateMaxUse(double intermediateMaxUse) {
		this.intermediateMaxUse = intermediateMaxUse;
	}


	public double getIntermediateMaxUse() {
		return intermediateMaxUse;
	}


	public void setIntermediateCost(double intermediateCost) {
		this.intermediateCost = intermediateCost;
	}


	public double getIntermediateCost() {
		return intermediateCost;
	}


	public void setIntermediateMinCost(double intermediateMinCost) {
		this.intermediateMinCost = intermediateMinCost;
	}


	public double getIntermediateMinCost() {
		return intermediateMinCost;
	}


	public void setIntermediateMedCost(double intermediateMedCost) {
		this.intermediateMedCost = intermediateMedCost;
	}


	public double getIntermediateMedCost() {
		return intermediateMedCost;
	}


	public void setIntermediateAvgCost(double intermediateAvgCost) {
		this.intermediateAvgCost = intermediateAvgCost;
	}


	public double getIntermediateAvgCost() {
		return intermediateAvgCost;
	}


	public void setIntermediateMaxCost(double intermediateMaxCost) {
		this.intermediateMaxCost = intermediateMaxCost;
	}


	public double getIntermediateMaxCost() {
		return intermediateMaxCost;
	}


	public SortedMap<Long, Long> getWaitingTime() {
		return Collections.unmodifiableSortedMap(waitedTime);
	}
}
