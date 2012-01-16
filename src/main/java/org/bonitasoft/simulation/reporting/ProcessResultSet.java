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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bonitasoft.simulation.model.process.SimProcess;

/**
 * @author Romain Bioteau
 *
 */
public class ProcessResultSet {

	private long instanceTime ;
	private long totalTime ;
	private long totalAvgTime ;
	private long totalMedTime ;
	private long totalMinTime = Long.MAX_VALUE;
	private long totalMaxTime  ;
	private long intermediateTime ;
	private long intermediateAvgTime ;
	private long intermediateMedTime ;
	private long intermediateMinTime = Long.MAX_VALUE;
	private long intermediateMaxTime  ;

	private SortedMap<Long, Long> avgTime ;
	private SortedMap<Long, Long> maxTime ;
	private SortedMap<Long, Long> medTime ;
	private SortedMap<Long, Long> minTime ;

	private long instanceWaitingTime  ;
	private long totalWaitingTime  ;
	private long totalAvgWaitingTime  ;
	private long totalMedWaitingTime  ;
	private long totalMinWaitingTime = Long.MAX_VALUE ;
	private long totalMaxWaitingTime  ;

	private long intermediateWaitingTime  ;
	private long intermediateAvgWaitingTime  ;
	private long intermediateMedWaitingTime  ;
	private long intermediateMinWaitingTime = Long.MAX_VALUE ;
	private long intermediateMaxWaitingTime  ;

	private Map<String, Double> instanceCost ;
	private Map<String, Double>  totalCost ;
	private Map<String, Double> minCost ;
	private Map<String, Double> avgCost ;
	private Map<String, Double> maxCost ;
	

	private SortedMap<Long, Long> avgWaitingTime  ;
	private SortedMap<Long, Long> maxWaitingTime  ;
	private SortedMap<Long, Long> medWaitingTime  ;
	private SortedMap<Long, Long> minWaitingTime  ;

	protected List<Long> intervalInstanceDuration;
	protected List<Long> intervalInstanceWaitingDuration;
	private int intervalTimeSize;
	private int totalInstancesCount;
	private SimProcess process;
	protected int nbInstanceOverMax;
	private Set<String> availableUnit;

	public int getNbInstanceOverMax() {
		return nbInstanceOverMax;
	}

	public ProcessResultSet(SimProcess process,Set<String> availableUnit){
		this.setProcess(process);

		intervalInstanceDuration = new ArrayList<Long>();
		intervalInstanceWaitingDuration = new ArrayList<Long>();

		avgTime = new TreeMap<Long,Long>() ;
		minTime = new TreeMap<Long,Long>() ;
		maxTime = new TreeMap<Long,Long>() ;
		medTime = new TreeMap<Long,Long>() ;

		avgWaitingTime = new TreeMap<Long,Long>() ;
		maxWaitingTime = new TreeMap<Long,Long>() ;
		minWaitingTime = new TreeMap<Long,Long>() ;
		medWaitingTime = new TreeMap<Long,Long>() ;
		
		instanceCost = new HashMap<String, Double>();
		totalCost  =  new HashMap<String, Double>();
		minCost = new HashMap<String, Double>();
		avgCost = new HashMap<String, Double>();
		maxCost = new HashMap<String, Double>();
		
		this.availableUnit = availableUnit ;
	}
	
	public Map<String, Double> getMinCost() {
		return Collections.unmodifiableMap(minCost);
	}

	public Map<String, Double> getAvgCost() {
		return Collections.unmodifiableMap(avgCost);
	}

	public Map<String, Double> getMaxCost() {
		return Collections.unmodifiableMap(maxCost);
	}

	public void updateInstanceCost(String unit,double cost) {
		if(instanceCost.get(unit) != null){
			instanceCost.put(unit, instanceCost.get(unit) + cost ) ;
		}else{
			instanceCost.put(unit, cost) ;
		}
	}
	
	public void updateCost() {
		for(String unit  : availableUnit){
			
			double instCost = 0;
			if(instanceCost.get(unit) != null){
				instCost = instanceCost.get(unit)  ;
			}
			if(minCost.get(unit) != null){
				minCost.put(unit, Math.min(minCost.get(unit), instCost)) ;
			}else{
				minCost.put(unit, instCost) ;
			}
			
			if(maxCost.get(unit) != null){
				maxCost.put(unit, Math.max(maxCost.get(unit), instCost)) ;
			}else{
				maxCost.put(unit, instCost) ;
			}
			
			if(totalCost.get(unit) != null){
				totalCost.put(unit, totalCost.get(unit) + instCost) ;
			}else{
				totalCost.put(unit, instCost) ;
			}
		}
		instanceCost.clear() ;
	}
	
	public void computeFinalCost() {
		for(String unit : totalCost.keySet()){
			double total = totalCost.get(unit) ;
			if(totalInstancesCount != 0 ){
				avgCost.put(unit, (double)total/(double)totalInstancesCount) ;
			}else{
				avgCost.put(unit, 0d) ;
			}
		}
	}

	public int getNumberOfInstances() {
		return totalInstancesCount;
	}

	/**
	 * @return the totalTime
	 */
	public long getTotalTime() {
		return totalTime;
	}


	/**
	 * @param totalTime the totalTime to set
	 */
	public void setTotalTime(long totalTime) {
		this.totalTime = totalTime;
	}


	/**
	 * @return the totalAvgTime
	 */
	public long getTotalAvgTime() {
		return totalAvgTime;
	}


	/**
	 * @param totalAvgTime the totalAvgTime to set
	 */
	public void setTotalAvgTime(long totalAvgTime) {
		this.totalAvgTime = totalAvgTime;
	}


	/**
	 * @return the totalMedTime
	 */
	public long getTotalMedTime() {
		return totalMedTime;
	}


	/**
	 * @param totalMedTime the totalMedTime to set
	 */
	public void setTotalMedTime(long totalMedTime) {
		this.totalMedTime = totalMedTime;
	}


	/**
	 * @return the totalMinTime
	 */
	public long getTotalMinTime() {
		return totalMinTime;
	}


	/**
	 * @param totalMinTime the totalMinTime to set
	 */
	public void setTotalMinTime(long totalMinTime) {
		this.totalMinTime = totalMinTime;
	}


	/**
	 * @return the totalMaxTime
	 */
	public long getTotalMaxTime() {
		return totalMaxTime;
	}


	/**
	 * @param totalMaxTime the totalMaxTime to set
	 */
	public void setTotalMaxTime(long totalMaxTime) {
		this.totalMaxTime = totalMaxTime;
	}


	/**
	 * @return the avgTime
	 */
	public SortedMap<Long, Long> getAvgTime() {
		return avgTime;
	}


	/**
	 * @param avgTime the avgTime to set
	 */
	public void setAvgTime(SortedMap<Long, Long> avgTime) {
		this.avgTime = avgTime;
	}


	/**
	 * @return the maxTime
	 */
	public SortedMap<Long, Long> getMaxTime() {
		return maxTime;
	}


	/**
	 * @param maxTime the maxTime to set
	 */
	public void setMaxTime(SortedMap<Long, Long> maxTime) {
		this.maxTime = maxTime;
	}


	/**
	 * @return the medTime
	 */
	public SortedMap<Long, Long> getMedTime() {
		return medTime;
	}


	/**
	 * @param medTime the medTime to set
	 */
	public void setMedTime(SortedMap<Long, Long> medTime) {
		this.medTime = medTime;
	}


	/**
	 * @return the minTime
	 */
	public SortedMap<Long, Long> getMinTime() {
		return minTime;
	}


	/**
	 * @param minTime the minTime to set
	 */
	public void setMinTime(SortedMap<Long, Long> minTime) {
		this.minTime = minTime;
	}


	/**
	 * @return the totalWaitingTime
	 */
	public long getTotalWaitingTime() {
		return totalWaitingTime;
	}


	/**
	 * @param totalWaitingTime the totalWaitingTime to set
	 */
	public void setTotalWaitingTime(long totalWaitingTime) {
		this.totalWaitingTime = totalWaitingTime;
	}


	/**
	 * @return the totalAvgWaitingTime
	 */
	public long getTotalAvgWaitingTime() {
		return totalAvgWaitingTime;
	}


	/**
	 * @param totalAvgWaitingTime the totalAvgWaitingTime to set
	 */
	public void setTotalAvgWaitingTime(long totalAvgWaitingTime) {
		this.totalAvgWaitingTime = totalAvgWaitingTime;
	}


	/**
	 * @return the totalMedWaitingTime
	 */
	public long getTotalMedWaitingTime() {
		return totalMedWaitingTime;
	}


	/**
	 * @param totalMedWaitingTime the totalMedWaitingTime to set
	 */
	public void setTotalMedWaitingTime(long totalMedWaitingTime) {
		this.totalMedWaitingTime = totalMedWaitingTime;
	}


	/**
	 * @return the totalMinWaitingTime
	 */
	public long getTotalMinWaitingTime() {
		return totalMinWaitingTime;
	}


	/**
	 * @param totalMinWaitingTime the totalMinWaitingTime to set
	 */
	public void setTotalMinWaitingTime(long totalMinWaitingTime) {
		this.totalMinWaitingTime = totalMinWaitingTime;
	}


	/**
	 * @return the totalMaxWaitingTime
	 */
	public long getTotalMaxWaitingTime() {
		return totalMaxWaitingTime;
	}


	/**
	 * @param totalMaxWaitingTime the totalMaxWaitingTime to set
	 */
	public void setTotalMaxWaitingTime(long totalMaxWaitingTime) {
		this.totalMaxWaitingTime = totalMaxWaitingTime;
	}


	/**
	 * @return the avgWaitingTime
	 */
	public SortedMap<Long, Long> getAvgWaitingTime() {
		return avgWaitingTime;
	}


	/**
	 * @param avgWaitingTime the avgWaitingTime to set
	 */
	public void setAvgWaitingTime(SortedMap<Long, Long> avgWaitingTime) {
		this.avgWaitingTime = avgWaitingTime;
	}


	/**
	 * @return the maxWaitingTime
	 */
	public SortedMap<Long, Long> getMaxWaitingTime() {
		return maxWaitingTime;
	}


	/**
	 * @param maxWaitingTime the maxWaitingTime to set
	 */
	public void setMaxWaitingTime(SortedMap<Long, Long> maxWaitingTime) {
		this.maxWaitingTime = maxWaitingTime;
	}


	/**
	 * @return the medWaitingTime
	 */
	public SortedMap<Long, Long> getMedWaitingTime() {
		return medWaitingTime;
	}


	/**
	 * @param medWaitingTime the medWaitingTime to set
	 */
	public void setMedWaitingTime(SortedMap<Long, Long> medWaitingTime) {
		this.medWaitingTime = medWaitingTime;
	}


	/**
	 * @return the minWaitingTime
	 */
	public SortedMap<Long, Long> getMinWaitingTime() {
		return minWaitingTime;
	}


	/**
	 * @param minWaitingTime the minWaitingTime to set
	 */
	public void setMinWaitingTime(SortedMap<Long, Long> minWaitingTime) {
		this.minWaitingTime = minWaitingTime;
	}

	public void setInstanceTime(long instanceTime) {
		this.instanceTime = instanceTime;
	}

	public long getInstanceTime() {
		return instanceTime;
	}

	public void setInstanceWaitingTime(long instanceWaitingTime) {
		this.instanceWaitingTime = instanceWaitingTime;
	}

	public long getInstanceWaitingTime() {
		return instanceWaitingTime;
	}

	public void updateTime() {
		long instanceDuration = getInstanceTime();

		intervalInstanceDuration.add(instanceDuration) ;
		intervalTimeSize ++ ;
		totalInstancesCount++ ;
	
		updateInstancesOverMax(instanceDuration);
	
		long total = getTotalTime() ;
		total = total + instanceDuration ;
		setTotalTime(total) ;

		long intermediateTime = getIntermediateTime() ;
		intermediateTime = intermediateTime + instanceDuration ;
		setIntermediateTime(intermediateTime) ;

		long max = getTotalMaxTime() ;
		if(max < instanceDuration){
			setTotalMaxTime(instanceDuration);
		}
		long intermediateMax = getIntermediateMaxTime() ;
		if(intermediateMax < instanceDuration){
			setIntermediateMaxTime(instanceDuration);
		}

		long min = getTotalMinTime() ;
		if(min > instanceDuration){
			setTotalMinTime(instanceDuration);
		}

		long intermediateMin = getIntermediateMinTime() ;
		if(intermediateMin > instanceDuration){
			setIntermediateMinTime(instanceDuration);
		}

		long avg = (long) (getIntermediateTime() / intervalTimeSize );
		setIntermediateAvgTime(avg) ;

		setInstanceTime(0) ;

	}

	protected void updateInstancesOverMax(long instanceDuration) {
		if(instanceDuration> getProcess().getMaximumTime()){
			nbInstanceOverMax  ++ ;
		}
	}

	public void updateWaitingTime() {
		long instanceWaitingDuration = getInstanceWaitingTime();
		intervalInstanceWaitingDuration.add(instanceWaitingDuration) ;

		long total = getTotalWaitingTime() ;
		total = total + instanceWaitingDuration ;
		setTotalWaitingTime(total) ;

		long intermediateWaintingTime = getIntermediateWaitingTime() ;
		intermediateWaintingTime = intermediateWaintingTime + instanceWaitingDuration ;
		setIntermediateWaitingTime(intermediateWaintingTime) ;

		long max = getTotalMaxWaitingTime() ;
		if(max < instanceWaitingDuration){
			setTotalMaxWaitingTime(instanceWaitingDuration);
		}

		long intermediateMax = getIntermediateMaxWaitingTime() ;
		if(intermediateMax < instanceWaitingDuration){
			setIntermediateMaxWaitingTime(instanceWaitingDuration);
		}

		long min = getTotalMinWaitingTime() ;
		if(min > instanceWaitingDuration){
			setTotalMinWaitingTime(instanceWaitingDuration);
		}
		long intermediateMin = getIntermediateMinWaitingTime() ;
		if(intermediateMin > instanceWaitingDuration){
			setIntermediateMinWaitingTime(instanceWaitingDuration);
		}

		long avg = (long) (getIntermediateWaitingTime() / intervalTimeSize );
		setIntermediateAvgWaitingTime(avg) ;

		setInstanceWaitingTime(0) ;

	}

	public void computeFinalTime() {

		if(totalInstancesCount > 0){
			long avg = (long) getTotalTime() / totalInstancesCount ;
			setTotalAvgTime(avg);
		}

		if(getTotalMinTime() == Long.MAX_VALUE){
			setTotalMinTime(0) ;
		}


	}

	public void computeFinalWaitingTime() {
		if(totalInstancesCount > 0){
			long avg = (long) getTotalWaitingTime() / totalInstancesCount ;
			setTotalAvgWaitingTime(avg);
		}

		if(getTotalMinTime() == Long.MAX_VALUE){
			setTotalMinTime(0) ;
		}
		
		if(getTotalMinWaitingTime() == Long.MAX_VALUE){
			setTotalMinWaitingTime(0) ;
		}


	}

	public void reset() {
		setIntermediateAvgTime(0);
		setIntermediateAvgWaitingTime(0);
		setIntermediateMaxTime(0);
		setIntermediateMaxWaitingTime(0);
		setIntermediateMedTime(0);
		setIntermediateMedWaitingTime(0);
		setIntermediateMinTime(Long.MAX_VALUE);
		setIntermediateMinWaitingTime(Long.MAX_VALUE);
		setIntermediateTime(0) ;
		setIntermediateWaitingTime(0);
		intervalTimeSize = 0 ;

		intervalInstanceWaitingDuration.clear() ;
		intervalInstanceDuration.clear() ;
	}

	public void setProcess(SimProcess process) {
		this.process = process;
	}

	public SimProcess getProcess() {
		return process;
	}

	public void setIntermediateTime(long intermediateTime) {
		this.intermediateTime = intermediateTime;
	}

	public long getIntermediateTime() {
		return intermediateTime;
	}

	public void setIntermediateMinTime(long intermediateMinTime) {
		this.intermediateMinTime = intermediateMinTime;
	}

	public long getIntermediateMinTime() {
		return intermediateMinTime;
	}

	public void setIntermediateMedTime(long intermediateMedTime) {
		this.intermediateMedTime = intermediateMedTime;
	}

	public long getIntermediateMedTime() {
		return intermediateMedTime;
	}

	public void setIntermediateAvgTime(long intermediateAvgTime) {
		this.intermediateAvgTime = intermediateAvgTime;
	}

	public long getIntermediateAvgTime() {
		return intermediateAvgTime;
	}

	public void setIntermediateMaxTime(long intermediateMaxTime) {
		this.intermediateMaxTime = intermediateMaxTime;
	}

	public long getIntermediateMaxTime() {
		return intermediateMaxTime;
	}

	public void setIntermediateWaitingTime(long intermediateWaitingTime) {
		this.intermediateWaitingTime = intermediateWaitingTime;
	}

	public long getIntermediateWaitingTime() {
		return intermediateWaitingTime;
	}

	public void setIntermediateMaxWaitingTime(long intermediateMaxWaitingTime) {
		this.intermediateMaxWaitingTime = intermediateMaxWaitingTime;
	}

	public long getIntermediateMaxWaitingTime() {
		return intermediateMaxWaitingTime;
	}

	public void setIntermediateMinWaitingTime(long intermediateMinWaitingTime) {
		this.intermediateMinWaitingTime = intermediateMinWaitingTime;
	}

	public long getIntermediateMinWaitingTime() {
		return intermediateMinWaitingTime;
	}

	public void setIntermediateMedWaitingTime(long intermediateMedWaitingTime) {
		this.intermediateMedWaitingTime = intermediateMedWaitingTime;
	}

	public long getIntermediateMedWaitingTime() {
		return intermediateMedWaitingTime;
	}

	public void setIntermediateAvgWaitingTime(long intermediateAvgWaitingTime) {
		this.intermediateAvgWaitingTime = intermediateAvgWaitingTime;
	}

	public long getIntermediateAvgWaitingTime() {
		return intermediateAvgWaitingTime;
	}


}
