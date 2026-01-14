// ============================
// Tree.java (refactored)
// ============================
package structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Binary tree that can be built from DB, searched in memory, and saved to DB.
 *
 * Refactoring goal (Task 1):
 * - Remove direct JDBC code from this class.
 * - Replace SMSController dependency with an abstraction (ISmsSender).
 * - Keep functionality unchanged (same behavior as before).
 */
public class Tree {

    String treeName;
    TreeNode root;

    // Dependencies injected from outside:
    private final ITreeDbGateway db;
    private final ISmsSender sms;
    private final String phone;

    /**
     * Main constructor using Dependency Injection.
     * Why it exists:
     * - In production: pass MySqlTreeDbGateway + SmsControllerAdapter.
     * - In tests: pass FakeTreeDbGateway + FakeSmsSender.
     */
    public Tree(String name, ITreeDbGateway db, ISmsSender sms, String phone) {
        this.treeName = name;
        this.root = null;
        this.db = db;
        this.sms = sms;
        this.phone = phone;
    }

    /**
     * Convenience constructor (optional).
     * Creates default dependencies for local running (TreeManager).
     * In unit tests you SHOULD NOT use this constructor, because it touches real DB.
     */
    public Tree(String name) {
        this(
            name,
            new MySqlTreeDbGateway(
                "jdbc:mysql://localhost/world?serverTimezone=IST&useSSL=false&allowPublicKeyRetrieval=true",
                "root",
                "Aa12345" // <-- change to your real password
            ),
            new SmsControllerAdapter(new SMSController("0541112233")),
            "0541112233"
        );
    }

    /**
     * Recursively searches for a node by name in the in-memory tree.
     * Behavior is identical to original code:
     * - DFS preorder-like: current -> left -> right
     * - Returns the first matching node found.
     */
    public TreeNode findNode(TreeNode current, String nodeName) {
        if (current == null) {
            return null;
        }
        if (current.nodeName.equals(nodeName)) {
            return current;
        }

        // Search left subtree first
        TreeNode leftSearch = findNode(current.left, nodeName);
        if (leftSearch != null) {
            return leftSearch;
        }

        // If not found on left, search right subtree
        return findNode(current.right, nodeName);
    }

    /**
     * Builds the tree from DB rows (table "tree").
     * Keeps the same idea as original:
     * 1) Create all nodes first (nodeMap)
     * 2) Link left/right pointers on second pass
     *
     * If DB operation fails, send SMS with the same message as in original code.
     */
    public void buildTreeFromDatabase() {
        Map<String, TreeNode> nodeMap = new HashMap<>();

        try {
            List<TreeRow> rows = db.selectTreeRows(this.treeName);

            // First pass: create node objects
            for (TreeRow r : rows) {
                nodeMap.put(r.nodeName, new TreeNode(r.nodeName, r.weight));
            }

            // Second pass: connect left/right pointers
            for (TreeRow r : rows) {
                TreeNode current = nodeMap.get(r.nodeName);

                if (r.leftp != null) {
                    current.left = nodeMap.get(r.leftp);
                }
                if (r.rightp != null) {
                    current.right = nodeMap.get(r.rightp);
                }

                // Note: This root logic is preserved from original code style.
                // It sets the first encountered node as root.
                if (this.root == null) {
                    this.root = current;
                }
            }
        } catch (Exception e) {
            sms.sendSMS("A problem with Database was found", phone);
            e.printStackTrace();
        }
    }

    /**
     * Saves a tree into the DB by inserting each node as a row.
     * IMPORTANT for Task 2:
     * - Must be testable without real DB, so it uses db.insertTreeRow().
     *
     * If an exception occurs, sends SMS message as original code.
     */
    public void saveNewTree(String treeName, TreeNode root) {
        try {
            if (root != null) {
                insertNode(treeName, root);
            }
        } catch (Exception e) {
            sms.sendSMS("A problem with saving tree in Database", phone);
            e.printStackTrace();
        }
    }

    /**
     * Recursive helper that performs preorder insertion:
     * - Insert current node row
     * - Insert left subtree
     * - Insert right subtree
     *
     * This preserves the original insertion order/behavior.
     */
    private void insertNode(String treeName, TreeNode node) throws Exception {
        if (node == null) return;

        String left = (node.left != null) ? node.left.nodeName : null;
        String right = (node.right != null) ? node.right.nodeName : null;

        db.insertTreeRow(new TreeRow(treeName, node.nodeName, node.weight, left, right));

        insertNode(treeName, node.left);
        insertNode(treeName, node.right);
    }

    /**
     * Inorder traversal printing nodes.
     * Not required for the homework tasks, but useful for manual debugging.
     */
    public void inorderTraversal(TreeNode node) {
        if (node != null) {
            inorderTraversal(node.left);
            System.out.println("Node: " + node.nodeName + ", Weight: " + node.weight);
            inorderTraversal(node.right);
        }
    }

    /**
     * Convenience overload to start inorder traversal from the root.
     */
    public void inorderTraversal() {
        if (this.root == null) {
            System.out.println("The tree is empty.");
        } else {
            inorderTraversal(this.root);
        }
    }
}























//old code!!
/*
package structure;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

class Tree {
	String treeName;
    TreeNode root;
    SMSController sc = new SMSController("0541112233");

    public Tree(String name) {
    	this.treeName = name;
        this.root = null;
    }

    // Recursive function to find the node by its name
    public TreeNode findNode(TreeNode current, String nodeName) {
        if (current == null) {
            return null;
        }
        if (current.nodeName.equals(nodeName)) {
            return current;
        }
        TreeNode leftSearch = findNode(current.left, nodeName);
        if (leftSearch != null) {
            return leftSearch;
        }
        return findNode(current.right, nodeName);
    }

         
    
    // Function to build the binary tree from the database
    public void buildTreeFromDatabase() {
        Map<String, TreeNode> nodeMap = new HashMap<>();
     
        try 
		{
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            System.out.println("Driver definition succeed");
        } catch (Exception ex) {*/
        	/* handle the error*/
  /*      	 System.out.println("Driver definition failed");
        	 }        
        try ( Connection connection = DriverManager.getConnection("jdbc:mysql://localhost/world?serverTimezone=IST","root","Aa12345");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM world.tree WHERE tree.treeName = '" + this.treeName + "';")) {

            // First pass: Create all nodes and store them in a map
            while (resultSet.next()) {
                String nodeName = resultSet.getString("nodename"); 
                int weight = resultSet.getInt("weight");
                nodeMap.put(nodeName, new TreeNode(nodeName, weight));
            }

            // Second pass: Link the nodes based on `left` and `right` columns
            resultSet.beforeFirst(); // Reset the cursor to the beginning
            while (resultSet.next()) {
                String nodeName = resultSet.getString("nodename");
                String leftName = resultSet.getString("leftp");
                String rightName = resultSet.getString("rightp");

                TreeNode currentNode = nodeMap.get(nodeName);
                if (leftName != null) {
                    currentNode.left = nodeMap.get(leftName);
                }
                if (rightName != null) {
                    currentNode.right = nodeMap.get(rightName);
                }

                // Set the root node (any node not listed as `left` or `right` remains the root)
                if (this.root == null) {
                    this.root = currentNode;
                }
            }
        } catch (Exception e) {
        	sc.sendSMS("A problem with Database was found",sc.getPhone());            
            e.printStackTrace();
        }
    }


	public void saveNewTree(String treeName, TreeNode root) {
	    try {
	        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
	        System.out.println("Driver definition succeed");

	        try (Connection connection = DriverManager.getConnection(
	                "jdbc:mysql://localhost/world?serverTimezone=IST&useSSL=false&allowPublicKeyRetrieval=true", "root", "Aa123456");
	             Statement statement = connection.createStatement()) {

	            if (root != null) {
	                insertNode(connection, statement, treeName, root);
	            }
	        }
	    } catch (Exception e) {
	    	sc.sendSMS("A problem with saving tree in Database",sc.getPhone());
	        e.printStackTrace();
	    }
	}

	private void insertNode(Connection connection, Statement statement, String treeName, TreeNode node) throws Exception {
	    if (node == null) return;

	    String left = (node.left != null) ? "'" + node.left.nodeName + "'" : "NULL";
	    String right = (node.right != null) ? "'" + node.right.nodeName + "'" : "NULL";

	    String query = String.format(
	        "INSERT INTO tree (treeName, nodeName, weight, leftp, rightp) VALUES ('%s', '%s', %d, %s, %s)",
	        treeName, node.nodeName, node.weight, left, right
	    );
	    statement.executeUpdate(query);

	    insertNode(connection, statement, treeName, node.left);
	    insertNode(connection, statement, treeName, node.right);
	}

    
    // Function for inorder traversal of the binary tree
    public void inorderTraversal(TreeNode node) {
        if (node != null) {
            inorderTraversal(node.left);
            System.out.println("Node: " + node.nodeName + ", Weight: " + node.weight);
            inorderTraversal(node.right);
        }
    }

    
    // Overloaded function to start traversal from the root
    public void inorderTraversal() {
        if (this.root == null) {
            System.out.println("The tree is empty.");
        } else {
            inorderTraversal(this.root);
        }
    }
    
}*/
