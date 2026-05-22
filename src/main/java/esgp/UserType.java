package esgp;
 
/**
 * UserType defines String constants representing the three possible
 * user types in the ESGP system, as specified in the SDS (section 4.2).
 */
public class UserType {
    public static final String ENCOST_UNVERIFIED = "encost-unverified";
    public static final String ENCOST_VERIFIED   = "encost-verified";
    public static final String COMMUNITY         = "community";
 
    private UserType() {}
}
