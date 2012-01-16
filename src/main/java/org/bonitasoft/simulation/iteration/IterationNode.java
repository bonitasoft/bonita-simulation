package org.bonitasoft.simulation.iteration;

import java.util.HashSet;
import java.util.Set;

public class IterationNode implements Comparable<IterationNode>{

	public static enum JoinType {
		AND, XOR
	}
	public static enum SplitType {
		AND, XOR
	}

	private String name;
	private JoinType joinType;
	private SplitType splitType;

	private Set<IterationTransition> incomingTransitions = new HashSet<IterationTransition>();
	private Set<IterationTransition> outgoingTransitions = new HashSet<IterationTransition>();

	public IterationNode(final String name) {
		super();
		this.name = name;
	}

	public IterationNode(final String name, final JoinType joinType, final SplitType splitType) {
		this(name);
		this.joinType = joinType;
		this.splitType = splitType;
	}
	public String getName() {
		return name;
	}
	public Set<IterationTransition> getOutgoingTransitions() {
		return outgoingTransitions;
	}
	public Set<IterationTransition> getIncomingTransitions() {
		return incomingTransitions;
	}
	public void addIncomingTransition(IterationTransition t) {
		this.incomingTransitions.add(t);
	}
	public void addOutgoingTransition(IterationTransition t) {
		this.outgoingTransitions.add(t);
	}
	public void removeOutgoingTransition(IterationTransition transition) {
		this.outgoingTransitions.remove(transition);
	}
	public void removeIncomingTransition(IterationTransition transition) {
		this.incomingTransitions.remove(transition);
	}
	public boolean hasOutgoingTransitions() {
		return !outgoingTransitions.isEmpty();
	}
	public boolean hasIncomingTransitions() {
		return !incomingTransitions.isEmpty();
	}
	public JoinType getJoinType() {
		return joinType;
	}
	public SplitType getSplitType() {
		return splitType;
	}

	@Override
	public String toString() {
		return "IterationNode [name=" + name
				+ ", joinType=" + joinType 
				+ ", splitType=" + splitType + "]";
	}


	public int compareTo(IterationNode anotherIterationNode) {
		return this.toString().compareTo(anotherIterationNode.toString());
	}

}
