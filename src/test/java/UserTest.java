import esgp.User;
import esgp.UserType;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link User#setUserType(String)} and {@link User#getUserType()}.
 */
public class UserTest {

    @Test
    @DisplayName("setUserType(community) – getUserType() returns 'community'")
    public void getUserTypeTestCommunityType() {
        User user = new User();
        user.setUserType(UserType.COMMUNITY);
        assertEquals(UserType.COMMUNITY, user.getUserType(),
                "The user type should be set to community.");
    }

    @Test
    @DisplayName("setUserType(encost-verified) – getUserType() returns 'encost-verified'")
    public void getUserTypeTestEncostVerifiedType() {
        User user = new User();
        user.setUserType(UserType.ENCOST_VERIFIED);
        assertEquals(UserType.ENCOST_VERIFIED, user.getUserType(),
                "The user type should be set to encost-verified.");
    }

    @Test
    @DisplayName("setUserType(encost-unverified) – getUserType() returns 'encost-unverified'")
    public void getUserTypeTestEncostUnverifiedType() {
        User user = new User();
        user.setUserType(UserType.ENCOST_UNVERIFIED);
        assertEquals(UserType.ENCOST_UNVERIFIED, user.getUserType(),
                "The user type should be set to encost-unverified.");
    }

    @Test
    @DisplayName("setUserType(invalid) – throws IllegalArgumentException")
    public void getUserTypeTestInvalidType() {
        User user = new User();
        assertThrows(IllegalArgumentException.class,
                () -> user.setUserType("invalid-type"),
                "setUserType should throw IllegalArgumentException for invalid user types.");
    }
}
