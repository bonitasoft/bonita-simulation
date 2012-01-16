package org.bonitasoft.simulation.model.loadprofile;

import org.bonitasoft.simulation.model.Period;
import org.bonitasoft.simulation.model.RepartitionType;

public class InjectionPeriod {
	
	private Period period ;
	private int numberOfInstance ;
	private RepartitionType repartition ;
	
	public InjectionPeriod(final Period period, final int numberOfInstance, final RepartitionType repartition){
	  this.period = period;
	  this.numberOfInstance = numberOfInstance;
	  this.repartition = repartition;
	}

	public Period getPeriod() {
		return period;
	}

	public int getNumberOfInstance() {
		return numberOfInstance;
	}

	public RepartitionType getRepartition() {
		return repartition;
	}

}
