package esgp;

import org.graphstream.graph.Graph;

/**
 * User is the base class for all ESGP users, as specified in the SDS
 * (Sections 3.4 and 4.1).
 *
 * <p>A User holds the shared state and behaviour available to both
 * Community users and Encost users:
 * <ul>
 *   <li>An {@link EncostDataStructure} instance used by all features.</li>
 *   <li>A {@link DataLoader} for parsing and loading the dataset into
 *       the data structure.</li>
 *   <li>A {@link GraphVisualiser} for displaying the node graph GUI.</li>
 * </ul>
 *
 * <p>Community users are represented directly by this class.
 * Encost users are represented by the {@link EncostUser} subclass.
 */
public class User {

    // The data structure that persists across the application's runtime
    protected EncostDataStructure encostDataStructure;

    // Responsible for parsing the dataset file and populating the data structure
    protected DataLoader dataLoader;

    // Responsible for displaying the GraphStream graph GUI
    protected GraphVisualiser graphVisualiser;

    // The user type constant from UserType
    private int userType;

    /**
     * Constructs a Community User.
     * Initialises the data structure, data loader, and graph visualiser
     * using the default ESHD file path.
     */
    public User() {
        this.userType          = UserType.COMMUNITY;
        this.encostDataStructure = new EncostDataStructure();
        this.dataLoader          = new DataLoader();
        this.graphVisualiser     = new GraphVisualiser();
    }

    // -----------------------------------------------------------------------
    // Getters & Setters
    // -----------------------------------------------------------------------

    /**
     * @return the user-type constant ({@link UserType}) for this session
     */
    public int getUserType() {
        return userType;
    }

    /**
     * Sets the user type.
     *
     * @param userType one of {@link UserType#COMMUNITY} or {@link UserType#ENCOST}
     * @throws IllegalArgumentException if the value is not a valid user-type constant
     */
    public void setUserType(int userType) {
        if (userType != UserType.COMMUNITY && userType != UserType.ENCOST) {
            throw new IllegalArgumentException(
                    "Invalid user type: " + userType
                    + ". Must be 0 (COMMUNITY) or 1 (ENCOST).");
        }
        this.userType = userType;
    }

    /**
     * @return the {@link EncostDataStructure} held by this user
     */
    public EncostDataStructure getEncostDataStructure() {
        return encostDataStructure;
    }

    /**
     * @return the {@link DataLoader} held by this user
     */
    public DataLoader getDataLoader() {
        return dataLoader;
    }

    /**
     * @return the {@link GraphVisualiser} held by this user
     */
    public GraphVisualiser getGraphVisualiser() {
        return graphVisualiser;
    }

    // -----------------------------------------------------------------------
    // Feature methods
    // -----------------------------------------------------------------------

    /**
     * Loads the dataset at the given file path into the Encost data structure.
     *
     * @param filePath path to the CSV/TSV/PSV dataset file
     */
    public void loadDataset(String filePath) {
        dataLoader.loadDataset(filePath, encostDataStructure);
    }

    /**
     * Launches the graph visualiser GUI using the current data structure.
     */
    public void viewGraphVisualiser() {
        graphVisualiser.displayGraph(encostDataStructure.getGraph());
    }

    @Override
    public String toString() {
        return "User{type=" + (userType == UserType.ENCOST ? "Encost" : "Community") + "}";
    }
}
