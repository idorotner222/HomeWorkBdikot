package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import structure.FakeSmsSender;
import structure.FakeTreeDbGateway;
import structure.Tree;
import structure.TreeNode;
import structure.TreeRow;

public class TreeFindNodeTest {

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
    void findNode_WhenRootIsNull_ReturnsNull() {
        FakeTreeDbGateway db = new FakeTreeDbGateway();
        FakeSmsSender sms = new FakeSmsSender();
        Tree t = new Tree("tree1", db, sms, "0541112233");

        assertNull(t.findNode(null, "A"));
    }

    @Test
    void findNode_WhenSearchIsRoot_ReturnsRoot() {
        FakeTreeDbGateway db = new FakeTreeDbGateway();
        FakeSmsSender sms = new FakeSmsSender();
        Tree t = new Tree("tree1", db, sms, "0541112233");

        TreeNode root = makeExampleTree();
        TreeNode found = t.findNode(root, "A");

        assertNotNull(found);
        assertEquals("A", found.nodeName);
    }

    @Test
    void findNode_WhenSearchInLeftSubtree_ReturnsCorrectNode() {
        FakeTreeDbGateway db = new FakeTreeDbGateway();
        FakeSmsSender sms = new FakeSmsSender();
        Tree t = new Tree("tree1", db, sms, "0541112233");

        TreeNode root = makeExampleTree();
        TreeNode found = t.findNode(root, "D");

        assertNotNull(found);
        assertEquals("D", found.nodeName);
        assertEquals(10, found.weight);
    }

    @Test
    void findNode_WhenSearchInRightSubtree_ReturnsCorrectNode() {
        FakeTreeDbGateway db = new FakeTreeDbGateway();
        FakeSmsSender sms = new FakeSmsSender();
        Tree t = new Tree("tree1", db, sms, "0541112233");

        TreeNode root = makeExampleTree();
        TreeNode found = t.findNode(root, "C");

        assertNotNull(found);
        assertEquals("C", found.nodeName);
        assertEquals(5, found.weight);
    }

    @Test
    void findNode_WhenNotFound_ReturnsNull() {
        FakeTreeDbGateway db = new FakeTreeDbGateway();
        FakeSmsSender sms = new FakeSmsSender();
        Tree t = new Tree("tree1", db, sms, "0541112233");

        TreeNode root = makeExampleTree();
        assertNull(t.findNode(root, "X"));
    }
}
