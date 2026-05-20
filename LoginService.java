package esgp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * LoginService handles the login prompt and credential authentication
 * for Encost users, as specified in the SDS (sections 2.1.2 and 4.2).
 *
 * Username and password are prompted on separate lines. If credentials
 * are invalid the prompt loops back indefinitely (no lockout), per the
 * SDS wireframe (section 4.7.2).
 *
 * Passwords in users.txt are treated as encrypted. The entered password
 * is encrypted before comparison, per SDS section 2.2.2.
 */
public class LoginService {

    private static final String USERS_FILE = "users.txt";

    private final Map<String, String> accounts = new HashMap<>();

    public LoginService() {
        loadAccounts();
    }

    /**
     * Displays the login prompt, asking for username then password on
     * separate lines. Loops until valid credentials are entered.
     *
     * @param scanner the shared Scanner over System.in
     * @return the authenticated username on success
     */
    public String loginPrompt(Scanner scanner) {
        System.out.println("Welcome Encost User please login");

        while (true) {
            System.out.println("Input your username:");
            String username = scanner.hasNextLine() ? scanner.nextLine().trim() : "";

            System.out.println("Input your password:");
            String password = scanner.hasNextLine() ? scanner.nextLine().trim() : "";

            if (authenticateUser(username, password)) {
                System.out.println("Welcome " + username);
                return username;
            } else {
                System.out.println("Invalid username or password please try again");
            }
        }
    }

    /**
     * Authenticates credentials against the loaded account store.
     * The entered password is encrypted before comparison.
     *
     * @param username the username to check
     * @param password the plaintext password to check
     * @return true if credentials match; false otherwise
     */
    public boolean authenticateUser(String username, String password) {
        if (username == null || username.isEmpty()
                || password == null || password.isEmpty()) {
            return false;
        }
        String encryptedInput = encrypt(password);
        String storedPassword = accounts.get(username);
        return storedPassword != null && storedPassword.equals(encryptedInput);
    }

    /**
     * Encrypts a plaintext password.
     * Uses Base64 encoding as a placeholder — replace with BCrypt in production.
     */
    private String encrypt(String plaintext) {
        return java.util.Base64.getEncoder().encodeToString(plaintext.getBytes());
    }

    /**
     * Loads username/password pairs from users.txt.
     * Passwords in the file are assumed to already be encrypted.
     * The header line is skipped. Malformed lines are silently ignored.
     */
    private void loadAccounts() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; }
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 2);
                if (parts.length == 2
                        && !parts[0].trim().isEmpty()
                        && !parts[1].trim().isEmpty()) {
                    accounts.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Warning: could not load " + USERS_FILE
                    + " – Encost login will not be available. ("
                    + e.getMessage() + ")");
        }
    }
}
