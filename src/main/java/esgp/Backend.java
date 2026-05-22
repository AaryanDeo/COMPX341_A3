package esgp;

import java.util.Scanner;

/**
 * Backend is the central component of ESGP, responsible for all
 * communication between the console and the rest of the system,
 * as specified in the SDS (section 4.2).
 *
 * <p>Implemented as a Singleton — only one instance exists at runtime.
 * The console calls {@link #main(String[])} which calls the other
 * functions in the correct order.
 *
 * <p>Attributes:
 * <ul>
 *   <li>{@code userType} — "community", "encost-unverified", or "encost-verified"</li>
 *   <li>{@code userInputScanner} — reads input from the console</li>
 *   <li>{@code accounts} — handled by {@link LoginService}</li>
 *   <li>{@code dataset} — the current {@link Dataset}</li>
 *   <li>{@code graphData} — the current {@link GraphDataType}</li>
 * </ul>
 */
public class Backend {

    // -----------------------------------------------------------------------
    // Singleton
    // -----------------------------------------------------------------------

    private static Backend backend;

    private String        userType;
    private Scanner       userInputScanner;
    private Dataset       dataset;
    private GraphDataType graphData;
    private LoginService  loginService;

    /**
     * Private constructor — initialises all attributes and creates the
     * default dataset, as per the SDS (section 4.2).
     */
    private Backend() {
        userInputScanner = new Scanner(System.in);
        loginService     = new LoginService();
        dataset          = new Dataset(); // default filepath set internally
        graphData        = new GraphDataType();
        dataset.createDataSet();
    }

    /**
     * Returns the single instance of Backend, creating it if necessary.
     *
     * @return the Backend singleton
     */
    public static Backend getInstance() {
        if (backend == null) {
            backend = new Backend();
        }
        return backend;
    }

    // -----------------------------------------------------------------------
    // Entry point
    // -----------------------------------------------------------------------

    /**
     * Main entry point. Called by the console to start the application.
     * Calls the other functions in the correct order.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        Backend b = Backend.getInstance();
        b.welcomePrompt();
    }

    // -----------------------------------------------------------------------
    // Prompt functions (SDS section 4.2)
    // -----------------------------------------------------------------------

    /**
     * Welcomes the user and asks for their user type (community or Encost),
     * storing the result in {@link #userType}.
     * As per the SDS wireframe (section 4.7.1).
     */
    public void welcomePrompt() {
        System.out.println("Welcome to the Encost Smart Graph Project");
        System.out.println("What type of user are you?");
        System.out.println("(a) An Encost User");
        System.out.println("(b) A Community User");
        System.out.print("Please input a or b: ");

        while (true) {
            String input = userInputScanner.hasNextLine()
                    ? userInputScanner.nextLine().trim().toLowerCase()
                    : "";

            if ("a".equals(input)) {
                userType = UserType.ENCOST_UNVERIFIED;
                loginPrompt();
                return;
            } else if ("b".equals(input)) {
                userType = UserType.COMMUNITY;
                esgpOptionsPrompt();
                return;
            } else {
                System.out.println("Invalid input. Please input a or b:");
            }
        }
    }

    /**
     * Displays the login prompt for Encost users. Loops until valid
     * credentials are entered, then updates userType to "encost-verified"
     * and proceeds to ESGP feature options.
     * As per the SDS (section 2.1.2 and wireframe 4.7.2).
     */
    public void loginPrompt() {
        loginService.loginPrompt(userInputScanner);
        userType = UserType.ENCOST_VERIFIED;
        esgpOptionsPrompt();
    }

    /**
     * Displays the ESGP feature options appropriate for the current user
     * type, reads the selection, and runs the chosen feature.
     * As per the SDS (section 2.1.3 and wireframe 4.7.3).
     */
    public void esgpOptionsPrompt() {
        boolean isEncost = UserType.ENCOST_VERIFIED.equals(userType);

        System.out.println("\nESGP Feature Options:");
        if (isEncost) {
            System.out.println("(a) loading a custom dataset");
            System.out.println("(b) visualising a graph representation of the data");
            System.out.println("(c) viewing summary statistics");
            System.out.print("Input the feature you would like to use a, b or c: ");
        } else {
            System.out.println("(b) visualising a graph representation of the data");
            System.out.print("Input the feature you would like to use b: ");
        }

        while (true) {
            String input = userInputScanner.hasNextLine()
                    ? userInputScanner.nextLine().trim().toLowerCase()
                    : "";

            if ("a".equals(input) && isEncost) {
                customDatasetPrompt();
                return;
            } else if ("b".equals(input)) {
                displayGraph();
                return;
            } else if ("c".equals(input) && isEncost) {
                statsPrompt();
                return;
            } else {
                System.out.println("Invalid input. Please choose from the options shown:");
            }
        }
    }

    /**
     * Prompts the user for a custom dataset file path. If valid, saves it
     * and sets it as the current dataset. If invalid, offers to try another
     * path or revert to the default dataset.
     * As per the SDS (section 2.1.5 and wireframe 4.7.4).
     */
    public void customDatasetPrompt() {
        System.out.print("Enter full path of custom dataset: ");

        while (true) {
            String path = userInputScanner.hasNextLine()
                    ? userInputScanner.nextLine().trim()
                    : "";

            Dataset custom = new Dataset();
            custom.setFileLocation(path);

            try {
                custom.createDataSet();
                dataset = custom;
                graphData.setDevices(dataset.getDevices());
                System.out.println("Dataset has been saved");
                esgpOptionsPrompt();
                return;
            } catch (RuntimeException e) {
                System.out.println("invalid dataset");
                System.out.println("Would you like to try:");
                System.out.println("(a) another custom dataset");
                System.out.println("(b) the default dataset");
                System.out.print("Please enter a or b: ");

                String choice = userInputScanner.hasNextLine()
                        ? userInputScanner.nextLine().trim().toLowerCase()
                        : "";

                if ("b".equals(choice)) {
                    dataset = new Dataset(); // reset to default
                    dataset.createDataSet();
                    graphData.setDevices(dataset.getDevices());
                    System.out.println("Default dataset restored.");
                    esgpOptionsPrompt();
                    return;
                } else {
                    System.out.print("Enter full path of custom dataset: ");
                }
            }
        }
    }

    /**
     * Creates a GraphDataType from the current dataset and displays it
     * in a UI window using GraphStream.
     * As per the SDS (section 2.1.8 and wireframe 4.7.5).
     */
    public void displayGraph() {
	System.setProperty("org.graphstream.ui", "swing");
        System.out.println("Loading graph visualisation...");
        graphData.setDevices(dataset.getDevices());
        graphData.displayGraph();
    }

    /**
     * Runs all three stat calculations and displays the results in the
     * console in a clear and concise manner.
     * As per the SDS (section 2.1.9 and wireframe 4.7.6).
     */
    public void statsPrompt() {
        System.out.println("\n==> Summary Statistics <==");
        System.out.println();
        System.out.println(dataset.calculateDistribution());
        System.out.println(dataset.calculateLocation());
        System.out.println(dataset.calculateConnectivity());
    }
}
