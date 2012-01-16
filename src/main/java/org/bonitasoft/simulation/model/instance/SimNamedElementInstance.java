package org.bonitasoft.simulation.model.instance;

import org.bonitasoft.simulation.model.process.SimNamedElement;

public abstract class SimNamedElementInstance {

	private SimNamedElement definition ;
	private String instanceUUID ;

	
	protected SimNamedElementInstance(SimNamedElement definition, String instanceUUID) {
    super();
    this.definition = definition;
    this.instanceUUID = instanceUUID;
  }

	public SimNamedElement getDefinition() {
		return definition;
	}

	public String getInstanceUUID() {
		return instanceUUID;
	}



	
}
