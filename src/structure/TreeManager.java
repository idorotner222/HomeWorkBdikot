package structure;
import structure.Tree;

public class TreeManager {

public static void main(String[] args) {
	        // Create the binary tree
	        Tree binaryTree = new Tree("tree1");

	        // Build the tree from the database
	        binaryTree.buildTreeFromDatabase(); 
	        System.out.println("Build finished. Root = " + (binaryTree.root != null ? binaryTree.root.nodeName : "null"));

	       

	    }
}
