package esgp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * LoginService handles the login prompt and credential authentication
 * for Encost users.
 *
 * Credentials are read from {@code users.txt} (comma-separated
 * {@code Username,Password} pairs). Authentication is case-sensitive.
 *
 * <p>Accepted input formats for {@link #promptLogin()}:
 * <ul>
 *   <li>{@code username password} (space-delimited)</li>
 *   <li>{@code username,password} (comma-delimited)</li>
 * </ul>
 * Any other format (newline-separated, no delimiter, empty) is considered invalid.
 *
 * <p>Brute-force protection: after {@value #MAX_ATTEMPTS} consecutive failed
 * authentication attempts the service refuses further attempts and prints a
 * lockout message.
 */
public class LoginService {

    /** Path to the credential store (relative to the working directory). */
    private static final String USERS_FILE = "users.txt";

    /** Maximum number of consecutive failed login attempts before lockout. */
    public static final int MAX_ATTEMPTS = 5;

    /** Lockout duration message displayed to the user (informational only). */
    private static final String LOCKOUT_MESSAGE =
            "Too many failed login attempts. Please try again after 5 minutes.";

    private final Map<String, String> credentials = new HashMap<>();
    private int failedAttempts = 0;
    private boolean lockedOut = false;

    /** Constructs a LoginService and loads credentials from {@value #USERS_FILE}. */
    public LoginService() {
        loadCredentials();
    }

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Displays a login prompt, reads one line from {@code System.in}, and
     * processes the credentials.
     *
     * <p>Outputs:
     * <ul>
     *   <li>{@code "authenticating"} prefix when valid format is detected</li>
     *   <li>{@code "invalid"} message when the format is unrecognised or empty</li>
     *   <li>Lockout message after {@value #MAX_ATTEMPTS} failures</li>
     * </ul>
     */
    public void promptLogin() {
        if (lockedOut) {
            System.out.println(LOCKOUT_MESSAGE);
            return;
        }

        System.out.println("Enter your credentials (username password  OR  username,password):");
        Scanner scanner = new Scanner(System.in);

        if (!scanner.hasNextLine()) {
            System.out.println("invalid input: no credentials provided.");
            return;
        }

        String line = scanner.nextLine().trim();

        if (line.isEmpty()) {
            System.out.println("invalid input: credentials cannot be empty.");
            return;
        }

        // Determine delimiter: space or comma
        String[] parts = null;
        if (line.contains(",")) {
            parts = line.split(",", 2);
        } else if (line.contains(" ")) {
            parts = line.split(" ", 2);
        }

        if (parts == null || parts.length != 2
                || parts[0].trim().isEmpty() || parts[1].trim().isEmpty()) {
            System.out.println("invalid input: use 'username password' or 'username,password'.");
            return;
        }

        String username = parts[0].trim();
        String password = parts[1].trim();

        System.out.println("authenticating...");
        boolean success = authenticateUser(username, password);

        if (!success) {
            failedAttempts++;
            System.out.println("Authentication failed: invalid username or password.");
            if (failedAttempts >= MAX_ATTEMPTS) {
                lockedOut = true;
                System.out.println(LOCKOUT_MESSAGE);
            }
        } else {
            failedAttempts = 0; // reset on success
            System.out.println("Authentication successful. Welcome, " + username + "!");
        }
    }

    /**
     * Authenticates the supplied credentials against the loaded user store.
     *
     * @param username the username to check
     * @param password the password to check
     * @return {@code true} if both match a record in the credential store;
     *         {@code false} otherwise (including empty strings)
     */
    public boolean authenticateUser(String username, String password) {
        if (username == null || username.isEmpty()
                || password == null || password.isEmpty()) {
            return false;
        }
        String stored = credentials.get(username);
        return stored != null && stored.equals(password);
    }

    // Internal helpers

    /**
     * Loads username/password pairs from {@value #USERS_FILE}.
     * Lines that do not contain a comma, or where either field is blank,
     * are silently skipped. The header line is also skipped.
     */
    private void loadCredentials() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {          // skip "Username,Password" header
                    firstLine = false;
                    continue;
                }
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",", 2);
                if (parts.length == 2
                        && !parts[0].trim().isEmpty()
                        && !parts[1].trim().isEmpty()) {
                    credentials.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            // Non-fatal: if the file is missing the credential map stays empty
            System.err.println("Warning: could not load " + USERS_FILE
                    + " – Encost user authentication will not be available. ("
                    + e.getMessage() + ")");
        }
    }
}
