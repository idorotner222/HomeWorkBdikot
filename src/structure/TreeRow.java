package structure;

public class TreeRow {
    public final String treeName;
    public final String nodeName;
    public final int weight;
    public final String leftp;
    public final String rightp;

    public TreeRow(String treeName, String nodeName, int weight, String leftp, String rightp) {
        this.treeName = treeName;
        this.nodeName = nodeName;
        this.weight = weight;
        this.leftp = leftp;
        this.rightp = rightp;
    }
}
