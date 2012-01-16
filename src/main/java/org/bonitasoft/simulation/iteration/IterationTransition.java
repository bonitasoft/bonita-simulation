package org.bonitasoft.simulation.iteration;

public class IterationTransition implements Comparable<IterationTransition>{

	IterationNode source;
	IterationNode destination;
	public IterationTransition(IterationNode source, IterationNode destination) {
		super();
		this.source = source;
		this.destination = destination;
	}
	public IterationNode getDestination() {
		return destination;
	}
	public IterationNode getSource() {
		return source;
	}
	@Override
	public String toString() {
		return "IterationTransition [destination=" + destination + ", source="
				+ source + "]";
	}

	public int compareTo(IterationTransition anotherTransition) {
		return this.toString().compareTo(anotherTransition.toString());
	}
}
