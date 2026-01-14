// ============================
// ITreeDbGateway.java
// ============================
package structure;

import java.util.List;

/**
 * Abstraction for DB operations needed by Tree.
 * Why it exists:
 * - Isolates JDBC dependency from Tree (important for unit testing).
 * - In tests, we can provide a FakeTreeDbGateway that stores inserted rows in memory.
 */
public interface ITreeDbGateway {

    /**
     * Reads all DB rows for a given tree name.
     * Used by buildTreeFromDatabase() to reconstruct the tree.
     */
    List<TreeRow> selectTreeRows(String treeName) throws Exception;

    /**
     * Inserts one row into the DB table.
     * Used by saveNewTree() recursion to persist the tree.
     */
    void insertTreeRow(TreeRow row) throws Exception;
}
