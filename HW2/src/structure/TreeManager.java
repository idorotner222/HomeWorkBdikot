package structure;

/**
 * TreeManager contains the main() method.
 * It is used ONLY for manual running and debugging,
 * not for unit testing.
 */
public class TreeManager {

    public static void main(String[] args) {

        // Create a Tree object with name "tree1".
        // This constructor uses REAL dependencies:
        // - real MySQL DB
        // - real SMSController
        Tree binaryTree = new Tree("tree1");

        // Build the tree from the database table "tree"
        binaryTree.buildTreeFromDatabase();

        // Print information to verify that the tree was built
        // If root is null -> tree was not built or DB returned no rows
        System.out.println(
            "Build finished. Root = " +
            (binaryTree.root != null ? binaryTree.root.nodeName : "null")
        );
    }
}
