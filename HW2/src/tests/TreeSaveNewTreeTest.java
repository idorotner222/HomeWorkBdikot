package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import structure.FakeSmsSender;
import structure.FakeTreeDbGateway;
import structure.Tree;
import structure.TreeNode;
import structure.TreeRow;

public class TreeSaveNewTreeTest {

    private Tree t;
    private FakeTreeDbGateway db;
    private FakeSmsSender sms;

    @BeforeEach
    // Resetting the test environment before each save operation [cite: 131]
    void setup() {
        db = new FakeTreeDbGateway();
        sms = new FakeSmsSender();
        t = new Tree("tree1", db, sms, "0541112233");
    }

    private TreeNode makeExampleTree() {
        TreeNode a = new TreeNode("A", 10);
        TreeNode b = new TreeNode("B", 20);
        TreeNode c = new TreeNode("C", 5);
        TreeNode d = new TreeNode("D", 10);
        a.left = b; a.right = c;
        b.left = d;
        return a;
    }

    @Test
    // Checking saveNewTree when root is null
    // Input: treeName = "treeX", root = null
    // Expected result: no rows inserted to DB and no SMS calls
    void saveNewTree_WhenRootIsNull_DoesNotInsertAnything_AndNoSms() {
        t.saveNewTree("treeX", null);

        assertEquals(0, db.insertedRows.size());
        assertEquals(0, sms.calls.size());
    }

    @Test
    // Checking saveNewTree with a valid tree (DB insert success)
    // Input: treeName = "treeX", root = A(10) with nodes {A,B,C,D}
    // Expected result: 4 rows inserted with correct links, no SMS calls
    void saveNewTree_WhenValidTree_InsertsAllNodesWithCorrectLinks() {
        TreeNode root = makeExampleTree();
        t.saveNewTree("treeX", root);

        assertEquals(4, db.insertedRows.size());
        assertEquals(0, sms.calls.size());

        TreeRow r0 = db.insertedRows.get(0);
        assertEquals("A", r0.nodeName);
        assertEquals("B", r0.leftp);
        assertEquals("C", r0.rightp);
    }

    @Test
    // Checking saveNewTree when DB insert throws an exception
    // Input: valid tree, db.throwOnInsert = true
    // Expected result: exactly one SMS call sent to "0541112233"
    void saveNewTree_WhenDbThrows_SendsSmsOnce() {
        db.throwOnInsert = true;

        t.saveNewTree("treeX", makeExampleTree());

        assertEquals(1, sms.calls.size());
        assertEquals("A problem with saving tree in Database", sms.calls.get(0).message);
        assertEquals("0541112233", sms.calls.get(0).phone);
    }

    @Test
    // Checking saveNewTree when a node has only a right child
    // Input: tree with root A and right child B
    // Expected result: two rows inserted, for node A: leftp == null, rightp == "B"
    void saveNewTree_WhenNodeHasOnlyRightChild_InsertsCorrectLinks() {
        TreeNode a = new TreeNode("A", 10);
        TreeNode b = new TreeNode("B", 20);
        a.right = b;

        t.saveNewTree("treeY", a);

        assertEquals(2, db.insertedRows.size());
        TreeRow r0 = db.insertedRows.get(0);
        assertNull(r0.leftp);
        assertEquals("B", r0.rightp);
    }

    @Test
    // Checking saveNewTree with a single-node tree
    // Input: treeName = "treeSingle", root = A only
    // Expected result: exactly one row inserted, pointers are null
    void saveNewTree_WhenSingleNodeTree_InsertsOneRow() {
        TreeNode a = new TreeNode("A", 10);

        t.saveNewTree("treeSingle", a);

        assertEquals(1, db.insertedRows.size());
        TreeRow r0 = db.insertedRows.get(0);
        assertNull(r0.leftp);
        assertNull(r0.rightp);
    }
}