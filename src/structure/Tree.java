//new code
package structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tree {
    String treeName;
    TreeNode root;

    private final ITreeDbGateway db;
    private final ISmsSender sms;
    private final String phone; // kept from your idea of sc.getPhone()

    // Production constructor
    public Tree(String name, ITreeDbGateway db, ISmsSender sms, String phone) {
        this.treeName = name;
        this.root = null;
        this.db = db;
        this.sms = sms;
        this.phone = phone;
    }

    // Optional convenience constructor if you want (not required)
    public Tree(String name) {
        this(
            name,
            new MySqlTreeDbGateway(
                "jdbc:mysql://localhost/world?serverTimezone=IST&useSSL=false&allowPublicKeyRetrieval=true",
                "root",
                "Aa123456"
            ),
            new SmsControllerAdapter(new SMSController("0541112233")),
            "0541112233"
        );
    }

    public TreeNode findNode(TreeNode current, String nodeName) {
        if (current == null) return null;
        if (current.nodeName.equals(nodeName)) return current;

        TreeNode leftSearch = findNode(current.left, nodeName);
        if (leftSearch != null) return leftSearch;

        return findNode(current.right, nodeName);
    }

    public void buildTreeFromDatabase() {
        Map<String, TreeNode> nodeMap = new HashMap<>();

        try {
            List<TreeRow> rows = db.selectTreeRows(this.treeName);

            // First pass: Create nodes
            for (TreeRow r : rows) {
                nodeMap.put(r.nodeName, new TreeNode(r.nodeName, r.weight));
            }

            // Second pass: Link nodes
            for (TreeRow r : rows) {
                TreeNode current = nodeMap.get(r.nodeName);
                if (r.leftp != null) current.left = nodeMap.get(r.leftp);
                if (r.rightp != null) current.right = nodeMap.get(r.rightp);

                if (this.root == null) {
                    this.root = current;
                }
            }
        } catch (Exception e) {
            sms.sendSMS("A problem with Database was found", phone);
            e.printStackTrace();
        }
    }

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

    private void insertNode(String treeName, TreeNode node) throws Exception {
        if (node == null) return;

        String left = (node.left != null) ? node.left.nodeName : null;
        String right = (node.right != null) ? node.right.nodeName : null;

        db.insertTreeRow(new TreeRow(treeName, node.nodeName, node.weight, left, right));

        insertNode(treeName, node.left);
        insertNode(treeName, node.right);
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
