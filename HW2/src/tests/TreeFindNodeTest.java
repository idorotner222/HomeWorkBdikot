package tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import structure.*;

public class TreeFindNodeTest {

    private Tree tree;
    private TreeNode root, nodeB, nodeC, nodeD;

    @BeforeEach
    void setup() {
        // Isolation: Using Manual Fakes as per Lec 05
        tree = new Tree("TestTree", new FakeTreeDbGateway(), new FakeSmsSender(), "0541112233");
        
        // Structure: A -> (B -> D), C
        root = new TreeNode("A", 10);
        nodeB = new TreeNode("B", 20);
        nodeC = new TreeNode("C", 5);
        nodeD = new TreeNode("D", 10);
        
        root.left = nodeB;
        root.right = nodeC;
        nodeB.left = nodeD;
    }

    /**
     * checking findNode when the root node is null
     * input: current = null, nodeName = "A"
     * expected: returns null safely
     */
    @Test
    void findNode_WhenRootIsNull_ReturnsNull() {
        assertNull(tree.findNode(null, "A"));
    }

    /**
     * checking findNode when searching for the root node
     * input: nodeName = "A"
     * expected: returns the root node object
     */
    @Test
    void findNode_WhenTargetIsRoot_ReturnsRoot() {
        assertEquals(root, tree.findNode(root, "A"));
    }

    /**
     * checking findNode when the target is deep in the left subtree
     * input: searching for "D" (nested child)
     * expected: returns node D
     */
    @Test
    void findNode_WhenTargetInLeftSubtree_ReturnsNodeD() {
        assertEquals(nodeD, tree.findNode(root, "D"));
    }

    /**
     * checking findNode when the target is in the right subtree
     * input: searching for "C"
     * expected: returns node C
     */
    @Test
    void findNode_WhenTargetInRightSubtree_ReturnsNodeC() {
        assertEquals(nodeC, tree.findNode(root, "C"));
    }

    /**
     * checking findNode when searching for a non-existent name
     * input: nodeName = "X"
     * expected: returns null after full traversal
     */
    @Test
    void findNode_WhenNameNotFound_ReturnsNull() {
        assertNull(tree.findNode(root, "X"));
    }

    /**
     * checking findNode with an empty string as input
     * input: nodeName = ""
     * expected: returns null (Boundary Case)
     */
    @Test
    void findNode_WhenNameIsEmpty_ReturnsNull() {
        assertNull(tree.findNode(root, ""));
    }

    /**
     * checking findNode when a node in the tree has a null name
     * input: target is "Target", but node in tree has name = null
     * expected: handles null name safely without NullPointerException
     */
    @Test
    void findNode_WhenNodeInTreeHasNullName_DoesNotCrash() {
        nodeC.nodeName = null;
        assertNull(tree.findNode(root, "Target"));
    }

    /**
     * checking findNode when the tree structure is circular (Failure Path)
     * input: nodeD points back to root creating a loop
     * expected: throws StackOverflowError (identifies recursion risk)
     */
    @Test
    void findNode_WhenTreeIsCircular_ThrowsStackOverflow() {
        nodeD.left = root; 
        assertThrows(StackOverflowError.class, () -> {
            tree.findNode(root, "NonExistent");
        });
    }
    /**
     * checking findNode case sensitivity (Demonstrating a Failed Test)
     * input: searching for "a" (lowercase) when the node name is "A" (uppercase)
     * expected: the test EXPECTS it to find the node, but the code returns null
     * result: FAIL (Red X) - this identifies a logic bug/limitation in the code
     */
    @Test
    void findNode_CaseSensitivity_ShouldFail() {
        // We expect the search to be smart and find "A" even if we search for "a"
        TreeNode found = tree.findNode(root, "a");
        
        // This assertion will fail because the code uses .equals() which is case-sensitive
        assertNotNull(found, "Bug: findNode should be case-insensitive but it failed to find the node");
    }
}