package esgp;
 
/**
 * User represents a session within ESGP.
 * userType is stored as a String per SDS section 4.2.
 */
public class User {
 
    private String username;
    private String userType;
 
    public User() {
        this.username = "";
        this.userType = UserType.COMMUNITY;
    }
 
    public User(String username, String userType) {
        this.username = username;
        setUserType(userType);
    }
 
    public String getUsername() { return username; }
 
    public String getUserType() { return userType; }
 
    public void setUserType(String userType) {
        if (!UserType.ENCOST_UNVERIFIED.equals(userType)
                && !UserType.ENCOST_VERIFIED.equals(userType)
                && !UserType.COMMUNITY.equals(userType)) {
            throw new IllegalArgumentException(
                    "Invalid user type: \"" + userType + "\". Must be \""
                    + UserType.ENCOST_UNVERIFIED + "\", \""
                    + UserType.ENCOST_VERIFIED + "\", or \""
                    + UserType.COMMUNITY + "\".");
        }
        this.userType = userType;
    }
 
    @Override
    public String toString() {
        return "User{username='" + username + "', type=" + userType + "}";
    }
}
