// ============================
// TreeRow.java
// ============================
package structure;

/**
 * Simple DTO representing one row in the DB table "tree".
 * Why it exists:
 * - Avoid ResultSet usage outside the DB layer.
 * - Allows Tree and DB gateway to communicate using plain Java objects.
 */
public class TreeRow {
    public final String treeName;
    public final String nodeName;
    public final int weight;
    public final String leftp;   // name of left child node, or null
    public final String rightp;  // name of right child node, or null

    public TreeRow(String treeName, String nodeName, int weight, String leftp, String rightp) {
        this.treeName = treeName;
        this.nodeName = nodeName;
        this.weight = weight;
        this.leftp = leftp;
        this.rightp = rightp;
    }
}
