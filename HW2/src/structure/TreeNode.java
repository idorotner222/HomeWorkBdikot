package structure;

/**
 * TreeNode represents a single node in a binary tree.
 * It contains:
 * - nodeName : identifier of the node (e.g. "A", "B")
 * - weight   : numeric value stored in the node
 * - left     : reference to left child
 * - right    : reference to right child
 *
 * IMPORTANT FOR TESTING:
 * Fields are public so test classes (in another package)
 * can easily build trees without getters/setters.
 */
public class TreeNode {

    // Name of the node (acts like a key)
    public String nodeName;

    // Weight/value of the node
    public int weight;

    // Left child
    public TreeNode left;

    // Right child
    public TreeNode right;

    /**
     * Constructor for creating a tree node.
     * Initially, left and right are null.
     */
    public TreeNode(String value, int weight) {
        this.nodeName = value;
        this.weight = weight;
    }
}














/*package structure;
 

public class TreeNode {
	    String nodeName;
	    int weight;
	    TreeNode left;
	    TreeNode right;

public  TreeNode(String value, int weight) {
	    	this.nodeName=value;
		    this.weight = weight;
	        this.left = null;
	        this.right = null;
	    }
}
*/