package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import structure.FakeSmsSender;
import structure.FakeTreeDbGateway;
import structure.Tree;
import structure.TreeNode;
import structure.TreeRow;


public class TreeSaveNewTreeTest {

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
    void saveNewTree_WhenRootIsNull_DoesNotInsertAnything_AndNoSms() {
        FakeTreeDbGateway db = new FakeTreeDbGateway();
        FakeSmsSender sms = new FakeSmsSender();
        Tree t = new Tree("tree1", db, sms, "0541112233");

        t.saveNewTree("treeX", null);

        assertEquals(0, db.insertedRows.size());
        assertEquals(0, sms.calls.size());
    }

    @Test
    void saveNewTree_WhenValidTree_InsertsAllNodesWithCorrectLinks() {
        FakeTreeDbGateway db = new FakeTreeDbGateway();
        FakeSmsSender sms = new FakeSmsSender();
        Tree t = new Tree("tree1", db, sms, "0541112233");

        TreeNode root = makeExampleTree();
        t.saveNewTree("treeX", root);

        // Expect 4 inserts: A, B, D, C (preorder recursion like your original code)
        assertEquals(4, db.insertedRows.size());
        assertEquals(0, sms.calls.size());

        TreeRow r0 = db.insertedRows.get(0);
        assertEquals("treeX", r0.treeName);
        assertEquals("A", r0.nodeName);
        assertEquals(10, r0.weight);
        assertEquals("B", r0.leftp);
        assertEquals("C", r0.rightp);

        TreeRow r1 = db.insertedRows.get(1);
        assertEquals("B", r1.nodeName);
        assertEquals("D", r1.leftp);
        assertNull(r1.rightp);

        TreeRow r2 = db.insertedRows.get(2);
        assertEquals("D", r2.nodeName);
        assertNull(r2.leftp);
        assertNull(r2.rightp);

        TreeRow r3 = db.insertedRows.get(3);
        assertEquals("C", r3.nodeName);
        assertNull(r3.leftp);
        assertNull(r3.rightp);
    }

    @Test
    void saveNewTree_WhenDbThrows_SendsSmsOnce() {
        FakeTreeDbGateway db = new FakeTreeDbGateway();
        db.throwOnInsert = true;

        FakeSmsSender sms = new FakeSmsSender();
        Tree t = new Tree("tree1", db, sms, "0541112233");

        t.saveNewTree("treeX", makeExampleTree());

        assertEquals(1, sms.calls.size());
        assertEquals("A problem with saving tree in Database", sms.calls.get(0).message);
        assertEquals("0541112233", sms.calls.get(0).phone);
    }
}
