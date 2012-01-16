package org.bonitasoft.simulation.model.instance;

import org.bonitasoft.simulation.model.process.SimData;

public class SimDataInstance extends SimNamedElementInstance{

	private Object value ;

	public SimDataInstance(final SimData definition, final String instanceUUID, final Object value){
		super(definition, instanceUUID);
		setValue(value);
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

}
