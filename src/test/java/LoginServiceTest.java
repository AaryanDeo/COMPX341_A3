import esgp.LoginService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link LoginService}.
 *
 * Tests verify separate username/password prompting and authentication,
 * as per the SDS wireframe (section 4.7.2).
 *
 * Assumes users.txt is present with encostUserA/password789 as valid credentials.
 */
class LoginServiceTest {

    private final ByteArrayOutputStream outContent  = new ByteArrayOutputStream();
    private final PrintStream           originalOut = System.out;
    private LoginService loginService;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        loginService = new LoginService();
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
        System.setIn(System.in);
    }

    // -----------------------------------------------------------------------
    // loginPrompt() tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("loginPrompt – valid credentials on separate lines → prints 'Welcome'")
    void loginPromptValidCredentials() {
        // Username on first line, password on second line
        String input = "encostUserA\npassword789\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        String result = loginService.loginPrompt(scanner);

        assertTrue(outContent.toString().contains("Welcome encostUserA"),
                "Should print welcome message on successful login.");
        assertEquals("encostUserA", result,
                "Should return the authenticated username.");
    }

    @Test
    @DisplayName("loginPrompt – invalid then valid credentials → loops and succeeds")
    void loginPromptInvalidThenValid() {
        // Wrong password first, then correct
        String input = "encostUserA\nwrongPassword\nencostUserA\npassword789\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(input.getBytes()));

        String result = loginService.loginPrompt(scanner);

        assertTrue(outContent.toString().contains("Invalid username or password"),
                "Should display invalid message on first attempt.");
        assertTrue(outContent.toString().contains("Welcome encostUserA"),
                "Should succeed on second attempt.");
        assertEquals("encostUserA", result);
    }

    // -----------------------------------------------------------------------
    // authenticateUser() tests
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("authenticateUser – valid credentials → returns true")
    void authenticateUserValidCredentials() {
        assertTrue(loginService.authenticateUser("encostUserA", "password789"),
                "Authentication should succeed with valid credentials.");
    }

    @Test
    @DisplayName("authenticateUser – invalid credentials → returns false")
    void authenticateUserInvalidCredentials() {
        assertFalse(loginService.authenticateUser("invalidUser", "invalidPass"),
                "Authentication should fail with invalid credentials.");
    }

    @Test
    @DisplayName("authenticateUser – empty credentials → returns false")
    void authenticateUserEmptyCredentials() {
        assertFalse(loginService.authenticateUser("", ""),
                "Authentication should fail with empty credentials.");
    }
}
