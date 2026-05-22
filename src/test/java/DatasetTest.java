import esgp.Dataset;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Unit tests for {@link Dataset}.
 *
 * Verifies file loading, format validation, and all three summary
 * statistics methods, as per the SDS (section 4.3).
 */
public class DatasetTest {

    private Dataset dataset;
    private static final Path RESOURCE_PATH = Paths.get("src", "test", "resources");

    @BeforeEach
    void setUp() {
        dataset = new Dataset();
    }

    // -----------------------------------------------------------------------
    // setFileLocation / createDataSet
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("createDataSet – valid file → devices list is non-empty")
    void createDataSetValidFile() {
        dataset.setFileLocation(RESOURCE_PATH.resolve("validDataset.csv").toString());
        assertDoesNotThrow(() -> dataset.createDataSet(),
                "createDataSet should not throw for a valid dataset.");
        assertFalse(dataset.getDevices().isEmpty(),
                "Device list should be non-empty after loading valid file.");
    }

    @Test
    @DisplayName("createDataSet – invalid file path → throws RuntimeException")
    void createDataSetInvalidPath() {
        dataset.setFileLocation("nonexistent/path/file.csv");
        assertThrows(RuntimeException.class, () -> dataset.createDataSet(),
                "Should throw RuntimeException for invalid file path.");
    }

    @Test
    @DisplayName("createDataSet – corrupted file → throws RuntimeException")
    void createDataSetCorruptedFile() {
        dataset.setFileLocation(RESOURCE_PATH.resolve("corruptedDataset.csv").toString());
        assertThrows(RuntimeException.class, () -> dataset.createDataSet(),
                "Should throw RuntimeException for corrupted dataset.");
    }

    // -----------------------------------------------------------------------
    // Summary statistics
    // -----------------------------------------------------------------------

    @Test
    @DisplayName("calculateDistribution – contains category and type counts")
    void calculateDistributionContainsExpectedSections() {
        dataset.setFileLocation(RESOURCE_PATH.resolve("validDataset.csv").toString());
        dataset.createDataSet();
        String result = dataset.calculateDistribution();
        assertTrue(result.contains("Device Distribution"),
                "Should contain distribution header.");
        assertTrue(result.contains("Number of devices in each category"),
                "Should show category counts.");
        assertTrue(result.contains("Number of devices for each device type"),
                "Should show type counts.");
    }

    @Test
    @DisplayName("calculateLocation – contains household and region info")
    void calculateLocationContainsExpectedSections() {
        dataset.setFileLocation(RESOURCE_PATH.resolve("validDataset.csv").toString());
        dataset.createDataSet();
        String result = dataset.calculateLocation();
        assertTrue(result.contains("Device Location"),
                "Should contain location header.");
        assertTrue(result.contains("households"),
                "Should mention households.");
    }

    @Test
    @DisplayName("calculateConnectivity – contains router connectivity info")
    void calculateConnectivityContainsExpectedSections() {
        dataset.setFileLocation(RESOURCE_PATH.resolve("validDataset.csv").toString());
        dataset.createDataSet();
        String result = dataset.calculateConnectivity();
        assertTrue(result.contains("Device Connectivity"),
                "Should contain connectivity header.");
        assertTrue(result.contains("Average number of devices"),
                "Should show average connections.");
    }
}
