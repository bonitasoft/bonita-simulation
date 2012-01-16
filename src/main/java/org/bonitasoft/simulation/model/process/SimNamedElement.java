package org.bonitasoft.simulation.model.process;

public abstract class SimNamedElement {

	private String name ;
	
	
	protected SimNamedElement(String name) {
    super();
    this.name = name;
  }

	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof SimNamedElement){
			return getName().equals(((SimNamedElement) obj).getName()) ;
		}
		return super.equals(obj);
	}

}
