// ============================
// MySqlTreeDbGateway.java
// ============================
package structure;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Real DB gateway implementation using JDBC + MySQL.
 * Why it exists:
 * - Keeps all SQL + JDBC code in one place.
 * - Tree remains clean and testable.
 */
public class MySqlTreeDbGateway implements ITreeDbGateway {

    private final String url;
    private final String user;
    private final String pass;

    public MySqlTreeDbGateway(String url, String user, String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    /**
     * Reads all rows belonging to treeName.
     * Tree will then reconstruct node objects and connect left/right pointers.
     */
    /**
     * Retrieves all node records for a specific tree from the database.
     * * Documentation (as per Lab requirements):
     * - Functionality: Queries the 'tree' table for all rows matching the given treeName.
     * - Input: The name of the tree (String).
     * - Expected Result: A list of TreeRow objects containing nodeName, weight, 
     * and pointers (leftp, rightp).
     * * Refactoring Note: 
     * Replaced original Statement with PreparedStatement for security (Lec 04/10).
     * * @param treeName The name of the tree to retrieve.
     * @return A list of TreeRow objects representing the tree nodes.
     * @throws Exception if a database access error occurs.
     */
    @Override
    public List<TreeRow> selectTreeRows(String treeName) throws Exception {
        List<TreeRow> rows = new ArrayList<>();

        // 1. Load the MySQL JDBC Driver
        Class.forName("com.mysql.cj.jdbc.Driver");

        // 2. SQL Query definition (Parameterized to prevent SQL Injection)
        String query = "SELECT treeName, nodeName, weight, leftp, rightp FROM tree WHERE treeName = ?";

        // 3. Try-with-resources for automatic resource management
        try (Connection connection = DriverManager.getConnection(url, user, pass);
             PreparedStatement pstmt = connection.prepareStatement(query)) {
            
            // 4. Bind the treeName parameter to the query
            pstmt.setString(1, treeName);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // 5. Mapping database columns to the TreeRow DTO
                    rows.add(new TreeRow(
                            rs.getString("treeName"),
                            rs.getString("nodeName"),
                            rs.getInt("weight"),
                            rs.getString("leftp"),
                            rs.getString("rightp")
                    ));
                }
            }
        }
        return rows;
    }

    /**
     * Inserts a single TreeRow into the DB table.
     * This is called repeatedly by Tree.saveNewTree() (recursive insert).
     */
    @Override
    public void insertTreeRow(TreeRow row) throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             Statement statement = connection.createStatement()) {

            String left = (row.leftp != null) ? "'" + row.leftp + "'" : "NULL";
            String right = (row.rightp != null) ? "'" + row.rightp + "'" : "NULL";

            String query = String.format(
                    "INSERT INTO tree (treeName, nodeName, weight, leftp, rightp) VALUES ('%s', '%s', %d, %s, %s)",
                    row.treeName, row.nodeName, row.weight, left, right
            );

            statement.executeUpdate(query);
        }
    }
}
