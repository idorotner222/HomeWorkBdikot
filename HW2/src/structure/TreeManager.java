package structure;

/**
 * TreeManager Class
 * * This class serves as the entry point for the production environment.
 * It demonstrates the "Manual Dependency Injection" pattern where real
 * implementations are instantiated and provided to the Tree class.
 * * Architecture:
 * - Uses MySqlTreeDbGateway for real persistence.
 * - Uses SmsControllerAdapter to bridge the ISmsSender interface 
 * with the legacy SMSController.
 */
public class TreeManager {

    public static void main(String[] args) {
        
        // 1. Define real production parameters
        String dbUrl = "jdbc:mysql://localhost:3306/world";
        String dbUser = "root";
        String dbPass = "password";
        String targetPhone = "0541112233";

        try {
            // 2. Instantiate Real Dependencies (Production Implementations)
            // Following Lec 05: Interfaces allow us to swap Fakes with these Real objects.
            ITreeDbGateway dbGateway = new MySqlTreeDbGateway(dbUrl, dbUser, dbPass);
            
            // The Adapter Pattern allows using the legacy SMSController
            SMSController legacyController = new SMSController(targetPhone);
            ISmsSender smsSender = new SmsControllerAdapter(legacyController);

            // 3. Dependency Injection
            // Injecting the real DB and SMS components into the Tree instance.
            Tree binaryTree = new Tree("tree1", dbGateway, smsSender, targetPhone);

            // 4. Execution
            // Build the tree from the database table "tree"
            // Note: Ensure this method is correctly implemented in your Tree.java
            binaryTree.buildTreeFromDatabase();

            // Verification Output
            if (binaryTree.root != null) {
                System.out.println("Production build finished successfully.");
                System.out.println("Root node identified as: " + binaryTree.root.nodeName);
            } else {
                System.out.println("Build finished, but the tree is empty or root is null.");
            }

        } catch (Exception e) {
            // Global error handling for production runtime issues
            System.err.println("A critical error occurred during execution:");
            e.printStackTrace();
        }
    }
}