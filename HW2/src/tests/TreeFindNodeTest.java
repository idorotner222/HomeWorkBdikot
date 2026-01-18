package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import structure.FakeSmsSender;
import structure.FakeTreeDbGateway;
import structure.Tree;
import structure.TreeNode;

public class TreeFindNodeTest {

    private Tree t;
    private FakeTreeDbGateway db;
    private FakeSmsSender sms;

    @BeforeEach
    // Preparing objects for tests to avoid code duplication [cite: 124-126]
    void setup() {
        db = new FakeTreeDbGateway();
        sms = new FakeSmsSender();
        t = new Tree("tree1", db, sms, "0541112233");
    }

    private TreeNode makeExampleTree() {
        // Constructing a sample tree: A -> (B -> D), C
        TreeNode a = new TreeNode("A", 10);
        TreeNode b = new TreeNode("B", 20);
        TreeNode c = new TreeNode("C", 5);
        TreeNode d = new TreeNode("D", 10);
        a.left = b; a.right = c;
        b.left = d;
        return a;
    }

    @Test
    // Checking findNode when the root node is null
    // Input: current = null, nodeName = "A"
    // Expected result: returns null [cite: 50]
    void findNode_WhenRootIsNull_ReturnsNull() {
        assertNull(t.findNode(null, "A"));
    }

    @Test
    // Checking findNode when searching for the root node itself
    // Input: tree root = A(10) with children, nodeName = "A"
    // Expected result: returns the root node (nodeName == "A")
    void findNode_WhenSearchIsRoot_ReturnsRoot() {
        TreeNode root = makeExampleTree();
        TreeNode found = t.findNode(root, "A");

        assertNotNull(found);
        assertEquals("A", found.nodeName);
    }

    @Test
    // Checking findNode when the target node is in the left subtree
    // Input: tree root = A(10) with path A->B->D, nodeName = "D"
    // Expected result: returns node D (nodeName == "D" and weight == 10)
    void findNode_WhenSearchInLeftSubtree_ReturnsCorrectNode() {
        TreeNode root = makeExampleTree();
        TreeNode found = t.findNode(root, "D");

        assertNotNull(found);
        assertEquals("D", found.nodeName);
        assertEquals(10, found.weight);
    }

    @Test
    // Checking findNode when the target node is in the right subtree
    // Input: tree root = A(10) with right child C(5), nodeName = "C"
    // Expected result: returns node C (nodeName == "C" and weight == 5)
    void findNode_WhenSearchInRightSubtree_ReturnsCorrectNode() {
        TreeNode root = makeExampleTree();
        TreeNode found = t.findNode(root, "C");

        assertNotNull(found);
        assertEquals("C", found.nodeName);
        assertEquals(5, found.weight);
    }

    @Test
    // Checking findNode when the target node does not exist in the tree
    // Input: tree root = A(10) with nodes {A,B,C,D}, nodeName = "X"
    // Expected result: returns null
    void findNode_WhenNotFound_ReturnsNull() {
        TreeNode root = makeExampleTree();
        assertNull(t.findNode(root, "X"));
    }
}