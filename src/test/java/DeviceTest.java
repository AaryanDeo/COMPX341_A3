import esgp.Device;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Device}.
 *
 * Verifies correct parsing of CSV data lines and category determination,
 * as per the SDS (section 4.5).
 */
public class DeviceTest {

    @Test
    @DisplayName("Device constructor – valid data line parses all fields correctly")
    void deviceConstructorValidLine() {
        String line = "EWR-1234,01/04/22,Encost Router 360,Router,WKO-1234,-,Yes,Yes";
        Device d = new Device(line);

        assertEquals("EWR-1234",          d.getDeviceID());
        assertEquals("01/04/22",          d.getDateConnected());
        assertEquals("Encost Router 360", d.getName());
        assertEquals("Router",            d.getDeviceType());
        assertEquals("WKO-1234",          d.getHouseID());
        assertEquals("-",                 d.getRouterConnection());
        assertTrue(d.getSends());
        assertTrue(d.getReceives());
    }

    @Test
    @DisplayName("Device constructor – router device gets category 'Wifi Routers'")
    void deviceCategoryRouter() {
        Device d = new Device("EWR-1234,01/04/22,Encost Router 360,Router,WKO-1234,-,Yes,Yes");
        assertEquals("Wifi Routers", d.getCategory());
    }

    @Test
    @DisplayName("Device constructor – hub/controller gets category 'Hubs/Controllers'")
    void deviceCategoryHub() {
        Device d = new Device("EHC-2468,01/04/22,Encost Smart Hub 2.0,Hub/Controller,WKO-1234,EWR-1234,Yes,Yes");
        assertEquals("Hubs/Controllers", d.getCategory());
    }

    @Test
    @DisplayName("Device constructor – light bulb gets category 'Smart Lighting'")
    void deviceCategoryLighting() {
        Device d = new Device("ELB-4567,01/04/22,Encost Smart Bulb B22,Light Bulb,WKO-1234,EWR-1234,No,Yes");
        assertEquals("Smart Lighting", d.getCategory());
    }

    @Test
    @DisplayName("Device constructor – kettle gets category 'Smart Appliances'")
    void deviceCategoryAppliance() {
        Device d = new Device("EK-9876,07/05/22,Encost Smart Jug,Kettle,WKO-1234,EWR-1234,No,Yes");
        assertEquals("Smart Appliances", d.getCategory());
    }

    @Test
    @DisplayName("Device constructor – invalid line (wrong column count) throws IllegalArgumentException")
    void deviceConstructorInvalidLine() {
        assertThrows(IllegalArgumentException.class,
                () -> new Device("EWR-1234,01/04/22,only three fields"),
                "Should throw for a line with wrong number of fields.");
    }

    @Test
    @DisplayName("isRootDevice – router with '-' connection returns true")
    void isRootDeviceTrue() {
        Device d = new Device("EWR-1234,01/04/22,Encost Router 360,Router,WKO-1234,-,Yes,Yes");
        assertTrue(d.isRootDevice());
    }

    @Test
    @DisplayName("isRootDevice – non-root device returns false")
    void isRootDeviceFalse() {
        Device d = new Device("ELB-4567,01/04/22,Encost Smart Bulb,Light Bulb,WKO-1234,EWR-1234,No,Yes");
        assertFalse(d.isRootDevice());
    }

    @Test
    @DisplayName("getNodeShape – sends and receives → circle")
    void nodeShapeSendsAndReceives() {
        Device d = new Device("EWR-1234,01/04/22,Router,Router,WKO-1234,-,Yes,Yes");
        assertEquals("circle", d.getNodeShape());
    }

    @Test
    @DisplayName("getNodeShape – sends only → box (square)")
    void nodeShapeSendsOnly() {
        Device d = new Device("EWR-1234,01/04/22,Router,Router,WKO-1234,-,Yes,No");
        assertEquals("box", d.getNodeShape());
    }

    @Test
    @DisplayName("getNodeShape – receives only → triangle")
    void nodeShapeReceivesOnly() {
        Device d = new Device("ELB-4567,01/04/22,Bulb,Light Bulb,WKO-1234,EWR-1234,No,Yes");
        assertEquals("triangle", d.getNodeShape());
    }
}
