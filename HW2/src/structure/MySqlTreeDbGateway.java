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
    @Override
    public List<TreeRow> selectTreeRows(String treeName) throws Exception {
        List<TreeRow> rows = new ArrayList<>();

        // Load MySQL driver (kept similar to original code behavior).
        Class.forName("com.mysql.cj.jdbc.Driver").newInstance();

        try (Connection connection = DriverManager.getConnection(url, user, pass);
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(
                     "SELECT * FROM world.tree WHERE tree.treeName = '" + treeName + "';")) {

            while (rs.next()) {
                rows.add(new TreeRow(
                        rs.getString("treeName"),
                        rs.getString("nodename"),
                        rs.getInt("weight"),
                        rs.getString("leftp"),
                        rs.getString("rightp")
                ));
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
