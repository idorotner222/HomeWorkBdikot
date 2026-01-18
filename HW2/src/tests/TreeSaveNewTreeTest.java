package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structure.*;

public class TreeSaveNewTreeTest {

    private Tree tree;
    private FakeTreeDbGateway db;
    private FakeSmsSender sms;
    private TreeNode nodeA, nodeB, nodeC;

    @BeforeEach
    void setup() {
        db = new FakeTreeDbGateway();
        sms = new FakeSmsSender();
        tree = new Tree("tree1", db, sms, "0541112233");
        
        nodeA = new TreeNode("A", 10);
        nodeB = new TreeNode("B", 5);
        nodeC = new TreeNode("C", 8);
    }

    /**
     * checking saveNewTree when the root provided is null
     * input: root = null
     * expected: 0 rows inserted to the database
     */
    @Test
    void saveNewTree_NullRoot_DoesNothing() {
        tree.saveNewTree("treeX", null);
        assertEquals(0, db.insertedRows.size());
    }

    /**
     * checking saveNewTree with a single-node tree
     * input: nodeA (no children)
     * expected: 1 row inserted with null leftp and rightp
     */
    @Test
    void saveNewTree_SingleNode_InsertsCorrectly() {
        tree.saveNewTree("treeX", nodeA);
        assertEquals(1, db.insertedRows.size());
        assertNull(db.insertedRows.get(0).leftp);
        assertNull(db.insertedRows.get(0).rightp);
    }

    /**
     * checking saveNewTree when only a left child exists
     * input: nodeA.left = nodeB
     * expected: DB row shows leftp = "B" and rightp = null
     */
    @Test
    void saveNewTree_OnlyLeftChild_VerifiesPointers() {
        nodeA.left = nodeB;
        tree.saveNewTree("treeX", nodeA);
        assertEquals("B", db.insertedRows.get(0).leftp);
        assertNull(db.insertedRows.get(0).rightp);
    }

    /**
     * checking saveNewTree with a full tree structure
     * input: nodeA with left child B and right child C
     * expected: 3 rows inserted, A points to both B and C
     */
    @Test
    void saveNewTree_FullTree_InsertsAllNodes() {
        nodeA.left = nodeB;
        nodeA.right = nodeC;
        tree.saveNewTree("treeX", nodeA);
        assertEquals(3, db.insertedRows.size());
        assertEquals("B", db.insertedRows.get(0).leftp);
        assertEquals("C", db.insertedRows.get(0).rightp);
    }

    /**
     * checking saveNewTree when a database exception occurs
     * input: db.throwOnInsert = true
     * expected: exactly 1 SMS is sent to notify about the failure (Functional Equivalence)
     */
    @Test
    void saveNewTree_OnDbFailure_SendsSms() {
        db.throwOnInsert = true;
        tree.saveNewTree("treeX", nodeA);
        assertEquals(1, sms.calls.size());
        assertEquals("A problem with saving tree in Database", sms.calls.get(0).message);
    }

    /**
     * checking saveNewTree with extremely large weight values
     * input: node with Integer.MAX_VALUE
     * expected: saves successfully without data overflow (Boundary Case)
     */
    @Test
    void saveNewTree_WithMaximumWeight_SavesCorrectly() {
        TreeNode bigNode = new TreeNode("Big", Integer.MAX_VALUE);
        tree.saveNewTree("treeX", bigNode);
        assertEquals(Integer.MAX_VALUE, db.insertedRows.get(0).weight);
    }

    /**
     * checking saveNewTree when the node name is null
     * input: TreeNode with name = null
     * expected: row is saved with a null nodeName safely
     */
    @Test
    void saveNewTree_WhenNodeNameIsNull_SavesRow() {
        TreeNode nullNameNode = new TreeNode(null, 5);
        tree.saveNewTree("treeX", nullNameNode);
        assertEquals(1, db.insertedRows.size());
        assertNull(db.insertedRows.get(0).nodeName);
    }
    /**
     * checking saveNewTree failure path when database is down
     * input: throwOnInsert = true (simulating DB crash)
     * expected: test PASSES because the catch block correctly triggers the SMS
     */
    @Test
    void saveNewTree_FailurePath_SendsSmsSuccessfully() {
        db.throwOnInsert = true;
        tree.saveNewTree("treeX", nodeA);
        
        // The test passes if the code handled the failure by sending an SMS
        assertEquals(1, sms.calls.size());
        assertEquals("A problem with saving tree in Database", sms.calls.get(0).message);
    }
}