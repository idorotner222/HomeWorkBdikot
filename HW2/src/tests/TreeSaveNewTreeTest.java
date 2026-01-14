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
        //      A
        //     / \
        //    B   C
        //   /
        //  D
        TreeNode a = new TreeNode("A", 10);
        TreeNode b = new TreeNode("B", 20);
        TreeNode c = new TreeNode("C", 5);
        TreeNode d = new TreeNode("D", 10);
        a.left = b; a.right = c;
        b.left = d;
        return a;
    }

    @Test
    // checking saveNewTree when root is null (should do nothing)
    // input: treeName = "treeX", root = null
    // expected: no rows inserted to DB and no SMS calls
    void saveNewTree_WhenRootIsNull_DoesNotInsertAnything_AndNoSms() {
        FakeTreeDbGateway db = new FakeTreeDbGateway();
        FakeSmsSender sms = new FakeSmsSender();
        Tree t = new Tree("tree1", db, sms, "0541112233");

        t.saveNewTree("treeX", null);

        assertEquals(0, db.insertedRows.size());
        assertEquals(0, sms.calls.size());
    }

    @Test
    // checking saveNewTree with a valid tree (DB insert success)
    // input: treeName = "treeX", root = A(10) with nodes {A,B,C,D}
    // expected:
    // 1) inserts exactly 4 rows
    // 2) correct left/right pointers for A,B,D,C
    // 3) no SMS calls
    void saveNewTree_WhenValidTree_InsertsAllNodesWithCorrectLinks() {
        FakeTreeDbGateway db = new FakeTreeDbGateway();
        FakeSmsSender sms = new FakeSmsSender();
        Tree t = new Tree("tree1", db, sms, "0541112233");

        TreeNode root = makeExampleTree();
        t.saveNewTree("treeX", root);

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
    // checking saveNewTree when DB insert throws an exception
    // input: treeName = "treeX", valid root tree, db.throwOnInsert = true
    // expected:
    // 1) exactly one SMS call is sent
    // 2) message: "A problem with saving tree in Database"
    // 3) phone: "0541112233"
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
