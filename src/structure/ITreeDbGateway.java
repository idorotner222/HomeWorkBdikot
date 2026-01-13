package structure;

import java.util.List;

public interface ITreeDbGateway {
    // Returns rows of the tree table for a given treeName
    List<TreeRow> selectTreeRows(String treeName) throws Exception;

    // Inserts a single row
    void insertTreeRow(TreeRow row) throws Exception;
}
