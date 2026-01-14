package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import structure.FakeSmsSender;
import structure.FakeTreeDbGateway;
import structure.Tree;
import structure.TreeNode;

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
    // checking findNode when the root node is null
    // input: current = null, nodeName = "A"
    // expected: returns null
    void findNode_WhenRootIsNull_ReturnsNull() {
        FakeTreeDbGateway db = new FakeTreeDbGateway();
        FakeSmsSender sms = new FakeSmsSender();
        Tree t = new Tree("tree1", db, sms, "0541112233");

        assertNull(t.findNode(null, "A"));
    }

    @Test
    // checking findNode when searching for the root node itself
    // input: tree root = A(10) with children, nodeName = "A"
    // expected: returns the root node (nodeName == "A")
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
    // checking findNode when the target node is in the left subtree
    // input: tree root = A(10) with left path A->B->D, nodeName = "D"
    // expected: returns node D (nodeName == "D" and weight == 10)
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
    // checking findNode when the target node is in the right subtree
    // input: tree root = A(10) with right child C(5), nodeName = "C"
    // expected: returns node C (nodeName == "C" and weight == 5)
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
    // checking findNode when the target node does not exist in the tree
    // input: tree root = A(10) with nodes {A,B,C,D}, nodeName = "X"
    // expected: returns null
    void findNode_WhenNotFound_ReturnsNull() {
        FakeTreeDbGateway db = new FakeTreeDbGateway();
        FakeSmsSender sms = new FakeSmsSender();
        Tree t = new Tree("tree1", db, sms, "0541112233");

        TreeNode root = makeExampleTree();
        assertNull(t.findNode(root, "X"));
    }
}
