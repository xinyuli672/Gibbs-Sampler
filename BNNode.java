import java.util.ArrayList;

/**
 * A representation of a Bayesian network node. <b>You should use but not modify this class.</b>
 */


public class BNNode {
	/** list of all BNNodes */
	public static ArrayList<BNNode> nodes = new ArrayList<BNNode>();
	/** node variable name */
	public String name;
	/** node variable value */
	public boolean value;
	/** array of node parents (possibly empty) */
	public BNNode[] parents;
	/** array of node children (possibly empty) */
	public BNNode[] children;
	/** conditional probability table indexed by the binary number formed by concatenating Boolean bits from parent values (ordered by parents array), that is:
        parents[0].value?1:0 * 2^(p-1)
        + parents[1].value?1:0 * 2^(p-2)
        + ...
        + parents[p-1].value?1:0 * 1
	 */
	public double[] cpt;
	/** whether or not node is an evidence node */
	public boolean isEvidence = false;

	/**
	 * Compute the children from the parents.  A node with no children will have an empty array.
	 */
	public static void computeChildren() {
		ArrayList<BNNode> children = new ArrayList<BNNode>();
		// For each node, build an array of children by checking which nodes have it as a parent.
		for (BNNode node : nodes) {
			children.clear();
			for (BNNode node2 : nodes) {
				if (node == node2)
					continue;
				for (BNNode node3 : node2.parents)
					if (node3 == node)
						children.add(node2);
			}
			node.children = new BNNode[children.size()];
			node.children = (BNNode[]) children.toArray(node.children);
		}
	}

	/**
	 * Returns Conditional Probability Table (CPT) entry (probability of current variable being true) corresponding to current parent node values.
	 * To figure the mapping from parent values to table entry, imagine parent values as digits of a binary number.
	 * (The first/last parent is the most/least significant digit.)
	 * Convert this binary number to an integer, and you have the index to the CPT.
<pre>
Example: P(a|b,c) = {0.1, 0.2, 0.3, 0.4} means
         P(+a|-b -c) = 0.1   P(+a|-b +c) = 0.2   P(+a|+b -c) = 0.3   P(+a|+b +c) = 0.4
               0  0                0  1                1  0                1  1
                0                   1                   2                   3
cpt == {0.1, 0.2, 0.3, 0.4}
</pre>
	 * @return double CPT entry (probability of current variable being true) corresponding to current parent node values.
	 */
	public double cptLookup() {
		int entry = 0;
		int powerOfTwo = 1;
		for (int i = parents.length - 1; i >= 0; i--) {
			if (parents[i].value)
				entry += powerOfTwo;
			powerOfTwo *= 2;
		}
		return cpt[entry];
	}

	/**
	 * Print all information about the BN.
	 */
	public static void printBN() {
		for (BNNode node : nodes)
			System.out.println(node);
	}

	/**
	 * Return String representation of node.
	 * @return java.lang.String String representation of node
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder("BNNode " + name + ":\n");
		if (isEvidence)
			sb.append("  EVIDENCE\n");
		sb.append("  value: " + value + "\n");
		sb.append("  parents:");
		if (parents.length == 0)
			sb.append(" (none)");
		else
			for (BNNode parent : parents) 
				sb.append(" " + parent.name);
		sb.append("\n  children:");
		if (children.length == 0)
			sb.append(" (none)");
		else
			for (BNNode child : children) 
				sb.append(" " + child.name);
		sb.append("\n  CPT:");
		for (double cp : cpt)
			sb.append(" " + cp);
		return sb.toString();
	}
}





