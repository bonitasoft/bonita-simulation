/**
 * Copyright (C) 2011  BonitaSoft S.A.
 * BonitaSoft, 31 rue Gustave Eiffel - 38000 Grenoble
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation
 * version 2.1 of the License.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth
 * Floor, Boston, MA  02110-1301, USA.
 **/
package org.bonitasoft.simulation.iteration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bonitasoft.simulation.iteration.IterationNode.JoinType;
import org.bonitasoft.simulation.iteration.IterationNode.SplitType;


/**
 * @author Nicolas Chabanoles
 *
 */
public final class IterationDetection {
  
  private static final Logger LOG = Logger.getLogger(IterationDetection.class.getName());
  public static final String LINE_SEPARATOR = System.getProperty("line.separator");
  
  private IterationDetection() { }

  
  /*
   * Implements a deep first search to list all the paths in the process.<br/>
   * DFS (graph G, node s) {
   * 	Mark(s);
   * 	For each node n : Neighbor(s) do
   *  		if !marked(n) then
   *    		DFS(G,n);
   *  		End-if
   * 	end
   * }
   */
  public static SortedSet<IterationDescriptor> findIterations(final IterationProcess inProcess) {

	  final SortedMap<String, IterationNode> allProcessNodes = inProcess.getNodes();
	  final SortedSet<IterationNode> allPorcessNodesSorted = new TreeSet<IterationNode>(allProcessNodes.values());
	  
	  // store path during recursive search
	  final List<IterationNode> path = new ArrayList<IterationNode>(allProcessNodes.size());
	  // store visited nodes
	  final SortedSet<IterationNode> visitedNodes = new TreeSet<IterationNode>(); 
	  final SortedSet<IterationDescriptor> iterationDescriptors = new TreeSet<IterationDescriptor>(); 
	  for (IterationNode sourceNode : allPorcessNodesSorted) {
		  SortedSet<IterationTransition> incomingTransitions = new TreeSet<IterationTransition>(sourceNode.getIncomingTransitions());
		  final SortedSet<IterationDescriptor> temporaryIterationDescriptors = new TreeSet<IterationDescriptor>();
		  for (IterationTransition transition : incomingTransitions) {
			// List all paths form sourceNode to sourceNode, i.e. find all cycles.
			// We need to take care not to looping forever, that is why we need to remember already visited nodes.
			listCycles(inProcess, sourceNode, transition.getSource(), path, visitedNodes, temporaryIterationDescriptors);
			iterationDescriptors.addAll(temporaryIterationDescriptors);
		}
		  
	  }
    return iterationDescriptors;
  }
  

  private static void listCycles(IterationProcess inProcess,
		IterationNode sourceNode, IterationNode targetNode, List<IterationNode> path, SortedSet<IterationNode> visitedNodes, SortedSet<IterationDescriptor> iterationDescriptors) {
	  // Add node into the path.
	  path.add(sourceNode);
		 
		// SourceNode == targetNode --> stop recursive search
		if (sourceNode.getName().equals(targetNode.getName())) {
			final IterationDescriptor descriptor = buildIterationDescriptor(inProcess, path);
			iterationDescriptors.add(descriptor) ;
			path.remove(sourceNode);
			return;
		}
	 
		visitedNodes.add(sourceNode); // mark node
	 
		// search recursively...
		 SortedSet<IterationTransition> outgoingTransitions = new TreeSet<IterationTransition>(sourceNode.getOutgoingTransitions());
		  
		  for (IterationTransition transition : outgoingTransitions) {
			  if(!visitedNodes.contains(transition.getDestination())) {
				  // do not follow transitions that points to a visited node to avoid looping infinitely.
				  listCycles(inProcess, transition.getDestination(), targetNode, path, visitedNodes, iterationDescriptors);
			  }
		}
	 
		visitedNodes.remove(sourceNode); // un-mark node
		path.remove(sourceNode);
  }



   /*
    * Build an iteration descriptor based on nodes in path.
    * Entry nodes: nodes that have an incoming transition from a node that do not belong to the cycle.
    * Exit nodes: nodes that have an outgoing transition to a node that do not belong to the cycle.
    * Other nodes: nodes that have an incoming transition from a node that do not belong to the cycle.
    */
  private static IterationDescriptor buildIterationDescriptor(
		final IterationProcess inProcess, List<IterationNode> path) {

    final List<String> nodesInPath = getNodeNames(path);
    
	  final SortedSet<String> entryNodes = new TreeSet<String>();
      final SortedSet<String> exitNodes = new TreeSet<String>();
      final SortedSet<String> otherNodes = new TreeSet<String>();
      boolean hasEntryPointXor = false;
      for (final IterationNode node : path) {
    	  final Set<IterationTransition> incomingTransitions = node.getIncomingTransitions();
    	  final Set<IterationTransition> outgoingTransitions = node.getOutgoingTransitions();
    	  final String nodeName = node.getName();
        for (IterationTransition transition : incomingTransitions) {
    			if (!path.contains(transition.getSource())) {
    				hasEntryPointXor |= checkEntryNodeIntegrity(node);
    				entryNodes.add(nodeName);
    			}
    	  }
    	  for (IterationTransition transition : outgoingTransitions) {
    			if (!path.contains(transition.getDestination())) {
    				exitNodes.add(nodeName);
    				
    				final SplitType splitType = node.getSplitType();
            // Only allow XOR split for exit nodes
            if (!SplitType.XOR.equals(splitType) && LOG.isLoggable(Level.SEVERE)) {
              LOG.severe("Potential issue in iteration : " + nodeName + " is an exit node for cycle "
                  + nodesInPath + "." + LINE_SEPARATOR
                  + "Split type of this node is " + splitType + " but only XOR is supported." + LINE_SEPARATOR
                  + "An exception will be thrown at runtime if more than one transition is enabled at the same time.");
            }
    			}
    	  }
        if (!entryNodes.contains(nodeName) && !exitNodes.contains(nodeName)) {
          otherNodes.add(nodeName);
        }
      }
	  final IterationDescriptor itDescr = new IterationDescriptor(otherNodes, entryNodes, exitNodes);
	  checkCycleIntegrity(hasEntryPointXor, itDescr);
	  return itDescr;
  }


	private static List<String> getNodeNames(final List<IterationNode> path) {
	  final List<String> nodeNames = new ArrayList<String>();
	  for (final IterationNode iterationNode : path) {
      nodeNames.add(iterationNode.getName());
    }
    
    return nodeNames;
  }


  private static void checkCycleIntegrity(boolean hasEntryPointXor, final IterationDescriptor itDescr) throws RuntimeException {
		if (itDescr.getEntryNodes().size() == 0) {
	          throw new RuntimeException("Error in cycle detection : cycle " + itDescr + " has no start node");
	      }
	      if (!hasEntryPointXor) {
	          throw new RuntimeException("Error in cycle detection : cycle " + itDescr + " has no start node with a XOR join. Process execution can never enter this cycle.");
	      }
	}

	private static boolean checkEntryNodeIntegrity(IterationNode sourceNode) throws RuntimeException {
	    final JoinType joinType = sourceNode.getJoinType();
	    if (JoinType.AND.equals(joinType)) {
	    	throw new RuntimeException("Error in cycle detection : start node " + sourceNode.getName() + " has a AND join. This is not allowed.");
	    } 
	    return JoinType.XOR.equals(joinType);
	}
  
}
