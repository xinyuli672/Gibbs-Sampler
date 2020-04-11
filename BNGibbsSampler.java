import java.awt.print.Printable;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.text.PlainDocument;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of the Gibbs Sampling Stochastic Simulation method for
 * estimating Bayesian Network probabilities with/without evidence. <b>You
 * should only modify the simulate method.</b> Algorithm from Section 4.4.3 of
 * Pearl, Judea. "Probabilistic Reasoning in Intelligent Systems"
 */

public class BNGibbsSampler {
	/** iteration frequency of progress reports */
	public static int reportFrequency = 200000;
	/** total iterations; each non-evidence variable is updated in each iteration */
	public static int iterations = 1000000;

	/**
	 * Initialize parameters, parse input, display BN information, and perform Gibbs
	 * sampling. <b>You should not modify this method</b>
	 * 
	 * @param args an array of command-line arguments
	 * @throws ParseException standard input does not match grammar for Bayesian
	 *                        network specification. (See assignment documentation
	 *                        for BNF grammar.)
	 */
	public static void main(java.lang.String[] args) throws ParseException {
		// Initialize iterations and update frequency
		if (args.length > 0) {
			iterations = Integer.parseInt(args[0]);
			reportFrequency = (args.length > 1) ? Integer.parseInt(args[1]) : iterations;
		}

		// Read in belief net specification from System.in
		new BNParse(System.in).parseInput();
		BNNode.printBN();

		// Do stochastic simulation.
		simulate();
	}

	/**
	 * Perform Stochastic Simulation as described in Section 4.4.3 of Pearl, Judea.
	 * "Probabilistic Reasoning in Intelligent Systems". The enclosed file pearl.out
	 * shows the output format given the input: java BNGibbsSampler 1000000 200000
	 * &lt; sample.in &gt; sample.out <b>This is the only method you should
	 * modify.</b>
	 * 
	 */
	public static void simulate() {

		// Builds a list of nonevidence nodes and randomly assign them values
		List<String> evidenceList = new ArrayList<String>();
		List<String> nonevidenceList = new ArrayList<String>();
		Random rand = new Random();
		HashMap<String, Integer> hmap = new HashMap<String, Integer>();
		for (BNNode node : BNNode.nodes) {

			if (node.isEvidence) {
				evidenceList.add(node.name);
			} else {
				hmap.put(node.name, 0);
				nonevidenceList.add(node.name);
				int value = rand.nextInt(2);
				if (value == 0) {
					node.value = false;
				} else {
					node.value = true;
				}
			}
		}

		// After initializing statistics for each nonevidence node.
		BNNode.printBN();

		for (int j = 1; j <= iterations; j++) {
			if (j % reportFrequency == 0) {
				System.out.println("After iteration " + j + ":");
				System.out.println("Variable, Average Conditional Probability, Fraction True");
				for (BNNode node : BNNode.nodes) {
					if (!node.isEvidence) {
						System.out.println(
								"Variable: " + node.name + "Fraction True: " + (1.0 * hmap.get(node.name)) / (1.0 * j));
					}
				}
			}

			for (BNNode node : BNNode.nodes) {
				double outputtrue = 0.0;
				double outputfalse = 0.0;
				if (!node.isEvidence) {
					// System.out.println(node.name);
					node.value = true;
					BNNode[] childrenList = node.children;
					outputtrue = node.cptLookup();
					for (BNNode childnode : childrenList) {
						// System.out.println(childnode);
						if (childnode.value == true) {
							outputtrue = outputtrue * childnode.cptLookup();
						} else {
							outputtrue = outputtrue * (1 - childnode.cptLookup());
						}
					}
					System.out.println(node.name + outputtrue);

					node.value = false;
					outputfalse = 1 - node.cptLookup();
					for (BNNode childnode : childrenList) {
						// System.out.println(childnode);
						if (childnode.value == true) {
							outputfalse = outputfalse * childnode.cptLookup();
						} else {
							outputfalse = outputfalse * (1 - childnode.cptLookup());
						}
					}
					System.out.println(node.name + outputfalse);
					BNNode[] parentList = node.parents;
					String indexstr = "";
					for (BNNode parentnode : parentList) {
						// System.out.println(parentnode.name + parentnode.value);
						if (parentnode.value) {
							indexstr = indexstr + "1";
						} else {
							indexstr = indexstr + "0";
						}
					}
					// System.out.println(indexstr);
					int index = 0;
					if (!indexstr.isEmpty()) {
						index = Integer.parseInt(indexstr, 2);
					}
					// System.out.println(node.name + index);
					// System.out.println(outputtrue / (outputtrue + outputfalse));
					node.cpt[index] = outputtrue / (outputtrue + outputfalse);

					if (node.cpt[index] >= 0.5) {
						node.value = true;
						hmap.put(node.name, hmap.get(node.name) + 1);
					} else {
						node.value = false;
					}
				}

			}
		}

		// ***INSERT YOUR CODE HERE***
		// My commented implementation was about 60 lines.

	}
}
