package structure;

import java.util.ArrayList;
import java.util.List;

public class FakeTreeDbGateway implements ITreeDbGateway {
    public final List<TreeRow> insertedRows = new ArrayList<>();
    public boolean throwOnInsert = false;

    @Override
    public List<TreeRow> selectTreeRows(String treeName) {
        throw new UnsupportedOperationException("Not needed for these tests");
    }

    @Override
    public void insertTreeRow(TreeRow row) throws Exception {
        if (throwOnInsert) throw new Exception("DB insert failed");
        insertedRows.add(row);
    }
}
