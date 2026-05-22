package esgp;

/**
 * UserType defines integer constants representing the two possible
 * user types in the ESGP system, as specified in the SDS (Section 3.4).
 *
 * <ul>
 *   <li>{@code COMMUNITY} – A guest/community user. No login required.
 *       Has access to graph visualisation only.</li>
 *   <li>{@code ENCOST} – A verified Encost user. Must authenticate via
 *       {@link LoginManager}. Has access to all three features.</li>
 * </ul>
 */
public class UserType {
    public static final int COMMUNITY = 0;
    public static final int ENCOST    = 1;

    // Prevent instantiation
    private UserType() {}
}
