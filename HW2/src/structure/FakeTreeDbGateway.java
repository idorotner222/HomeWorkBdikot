// ============================
// FakeTreeDbGateway.java (for tests)
// ============================
package structure;

import java.util.ArrayList;
import java.util.List;

/**
 * Test Double for ITreeDbGateway.
 * Instead of touching a real DB, it stores inserted rows in memory.
 */
public class FakeTreeDbGateway implements ITreeDbGateway {

    public final List<TreeRow> insertedRows = new ArrayList<>();
    public boolean throwOnInsert = false;

    /**
     * Not needed for saveNewTree/findNode tests, so we can throw.
     * If you later test buildTreeFromDatabase, you can implement returning stubbed rows.
     */
    @Override
    public List<TreeRow> selectTreeRows(String treeName) {
        throw new UnsupportedOperationException("Not needed for these tests");
    }

    /**
     * Records inserted rows.
     * Can optionally throw to simulate DB failure and test SMS behavior.
     */
    @Override
    public void insertTreeRow(TreeRow row) throws Exception {
        if (throwOnInsert) {
            throw new Exception("DB insert failed");
        }
        insertedRows.add(row);
    }
}
