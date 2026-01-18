package structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Refactored Tree Class
 * * Documentation for buildTreeFromDatabase:
 * - Functionality: Reconstructs the binary tree from DB rows.
 * - Input: Uses the treeName provided in the constructor.
 * - Expected Result: The 'root' field is populated, and all child pointers (left/right) are linked.
 */
public class Tree {

    public String treeName;
    public TreeNode root;
    private final ITreeDbGateway db;
    private final ISmsSender sms;
    private final String phone;

    public Tree(String name, ITreeDbGateway db, ISmsSender sms, String phone) {
        this.treeName = name;
        this.root = null;
        this.db = db;
        this.sms = sms;
        this.phone = phone;
    }

    /**
     * Reconstructs the tree from the database using the Gateway.
     * This method follows a two-pass approach to ensure all nodes exist 
     * before linking them.
     */
    public void buildTreeFromDatabase() {
        try {
            // 1. Fetch data through the Interface Seam
            List<TreeRow> rows = db.selectTreeRows(this.treeName);
            Map<String, TreeNode> nodeMap = new HashMap<>();

            // 2. First Pass: Create all TreeNode objects
            for (TreeRow row : rows) {
                nodeMap.put(row.nodeName, new TreeNode(row.nodeName, row.weight));
            }

            // 3. Second Pass: Establish parent-child relationships
            for (TreeRow row : rows) {
                TreeNode current = nodeMap.get(row.nodeName);
                if (current != null) {
                    if (row.leftp != null) current.left = nodeMap.get(row.leftp);
                    if (row.rightp != null) current.right = nodeMap.get(row.rightp);
                }
            }

            // 4. Set the root of the tree (assuming the first row is the root)
            if (!rows.isEmpty()) {
                this.root = nodeMap.get(rows.get(0).nodeName);
            }

        } catch (Exception e) {
            // Error handling consistent with original requirements
            System.err.println("Error building tree from database:");
            e.printStackTrace();
        }
    }
    /**
     * Saves the tree to the database.
     * Requirement: Functionality must remain unchanged (triggers SMS on failure).
     */
    public void saveNewTree(String treeName, TreeNode root) {
        if (root == null) return;
        try {
            insertNode(treeName, root);
        } catch (Exception e) {
            sms.sendSMS("A problem with saving tree in Database", phone);
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

    public TreeNode findNode(TreeNode current, String nodeName) {
        if (current == null) return null;
        if (current.nodeName.equals(nodeName)) return current;
        TreeNode leftSearch = findNode(current.left, nodeName);
        if (leftSearch != null) return leftSearch;
        return findNode(current.right, nodeName);
    }
}