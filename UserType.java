package esgp;

/**
 * UserType defines integer constants representing the three possible
 * user types in the ESGP system.
 *
 * <ul>
 *   <li>{@code encostUnverified} – An Encost account holder whose credentials
 *       have NOT been verified against the users file. Gets graph visualisation only.</li>
 *   <li>{@code encostVerified} – An Encost account holder whose credentials
 *       HAVE been verified. Gets all three features.</li>
 *   <li>{@code community} – A guest/community user (no login). Gets graph
 *       visualisation only.</li>
 * </ul>
 */
public class UserType {
    public static final int encostUnverified = 0;
    public static final int encostVerified   = 1;
    public static final int community        = 2;

    // Prevent instantiation
    private UserType() {}
}
