package esgp;

/**
 * User represents a logged-in (or guest) session within ESGP.
 *
 * <p>Each User holds the username (empty string for community users) and
 * a {@link UserType} constant that controls which features are accessible.
 */
public class User {

    private String username;
    private int userType;

    /** Constructs a default User with no username and community access. */
    public User() {
        this.username = "";
        this.userType = UserType.community;
    }

    /**
     * Constructs a User with a specific username and type.
     *
     * @param username the authenticated username (empty string for community users)
     * @param userType one of the {@link UserType} constants
     * @throws IllegalArgumentException if {@code userType} is not a recognised constant
     */
    public User(String username, int userType) {
        this.username = username;
        setUserType(userType);
    }

    // -----------------------------------------------------------------------
    // Getters & Setters
    // -----------------------------------------------------------------------

    /** @return the username associated with this user session */
    public String getUsername() {
        return username;
    }

    /** @return the user-type constant ({@link UserType}) for this session */
    public int getUserType() {
        return userType;
    }

    /**
     * Sets the user type.
     *
     * @param userType one of {@link UserType#encostUnverified},
     *                 {@link UserType#encostVerified}, or {@link UserType#community}
     * @throws IllegalArgumentException if the value is not a valid user-type constant
     */
    public void setUserType(int userType) {
        if (userType != UserType.encostUnverified
                && userType != UserType.encostVerified
                && userType != UserType.community) {
            throw new IllegalArgumentException(
                    "Invalid user type: " + userType
                    + ". Must be 0 (encostUnverified), 1 (encostVerified), or 2 (community).");
        }
        this.userType = userType;
    }

    @Override
    public String toString() {
        String typeName;
        switch (userType) {
            case UserType.encostVerified:   typeName = "Encost Verified";   break;
            case UserType.encostUnverified: typeName = "Encost Unverified"; break;
            default:                        typeName = "Community";          break;
        }
        return "User{username='" + username + "', type=" + typeName + "}";
    }
}
