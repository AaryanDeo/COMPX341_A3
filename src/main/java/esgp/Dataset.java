package esgp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dataset manages loading and processing of an Encost device CSV file,
 * as specified in the SDS (section 4.3).
 *
 * <p>The default filepath points to the bundled Encost Smart Homes Dataset.
 * A custom path can be set via {@link #setFileLocation(String)}.
 *
 * <p>Summary statistics methods return formatted Strings for console display,
 * as per the SDS wireframe (section 4.7.6).
 */
public class Dataset {

    private String filepath = "Encost_Smart_Homes_Dataset__bigger_.txt";
    private String dataLine;
    private BufferedReader reader;
    private ArrayList<Device> devices = new ArrayList<>();

    // -----------------------------------------------------------------------
    // File management
    // -----------------------------------------------------------------------

    public void setFileLocation(String filepath) {
        this.filepath = filepath;
    }

    public String getFilepath() {
        return filepath;
    }

    // -----------------------------------------------------------------------
    // Dataset loading
    // -----------------------------------------------------------------------

    /**
     * Creates an ArrayList of {@link Device} objects from the file at
     * {@link #filepath}, storing the result in devices.
     *
     * @throws RuntimeException if the file cannot be read or is malformed
     */
    public void createDataSet() {
        devices.clear();
        try {
            reader = new BufferedReader(new FileReader(filepath));
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new RuntimeException("Dataset file is empty: " + filepath);
            }
            while ((dataLine = reader.readLine()) != null) {
                dataLine = dataLine.trim();
                if (dataLine.isEmpty()) continue;
                try {
                    devices.add(new Device(dataLine));
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException(
                            "Invalid dataset format in file: " + filepath
                            + " — " + e.getMessage(), e);
                }
            }
            if (devices.isEmpty()) {
                throw new RuntimeException(
                        "Dataset file contains no device records: " + filepath);
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "Unable to read dataset file: " + filepath
                    + " (" + e.getMessage() + ")", e);
        } finally {
            if (reader != null) {
                try { reader.close(); } catch (IOException ignored) {}
            }
        }
    }

    public ArrayList<Device> getDevices() {
        return devices;
    }

    // -----------------------------------------------------------------------
    // Summary statistics (SDS section 4.3 and wireframe 4.7.6)
    // -----------------------------------------------------------------------

    /**
     * Calculates device distribution statistics.
     * Reports number of devices in each category and each device type.
     *
     * @return formatted distribution statistics string
     */
    public String calculateDistribution() {
        Map<String, Integer> categoryCounts = new LinkedHashMap<>();
        Map<String, Integer> typeCounts     = new LinkedHashMap<>();
        for (Device d : devices) {
            categoryCounts.merge(d.getCategory(),  1, Integer::sum);
            typeCounts.merge(d.getDeviceType(),    1, Integer::sum);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Device Distribution:\n");
        sb.append("Number of devices in each category:\n");
        for (Map.Entry<String, Integer> e : categoryCounts.entrySet()) {
            sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        sb.append("Number of devices for each device type:\n");
        for (Map.Entry<String, Integer> e : typeCounts.entrySet()) {
            sb.append("  ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Calculates device location statistics.
     * Reports number of households in each region of NZ.
     *
     * @return formatted location statistics string
     */
    public String calculateLocation() {
        Map<String, Integer> regionCounts    = new LinkedHashMap<>();
        Map<String, Integer> householdCounts = new LinkedHashMap<>();
        for (Device d : devices) {
            String houseID = d.getHouseID();
            householdCounts.merge(houseID, 1, Integer::sum);
            regionCounts.merge(deriveRegion(houseID), 1, Integer::sum);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Device Location:\n");
        sb.append("Number of households in each region of NZ:\n");
        for (Map.Entry<String, Integer> e : regionCounts.entrySet()) {
            sb.append("  ").append(e.getKey())
              .append(": ").append(e.getValue()).append(" household(s)\n");
        }
        sb.append("Total unique households: ").append(householdCounts.size()).append("\n");
        return sb.toString();
    }

    /**
     * Calculates device connectivity statistics.
     * Reports average devices connected to each Encost Wifi Router.
     *
     * @return formatted connectivity statistics string
     */
    public String calculateConnectivity() {
        int routerCount      = 0;
        int connectedDevices = 0;
        for (Device d : devices) {
            if ("Wifi Routers".equals(d.getCategory())) routerCount++;
            if (!d.isRootDevice())                      connectedDevices++;
        }
        double avg = routerCount > 0 ? (double) connectedDevices / routerCount : 0.0;
        StringBuilder sb = new StringBuilder();
        sb.append("Device Connectivity:\n");
        sb.append(String.format(
                "Average number of devices that an Encost Wifi Router is connected to: %.2f%n",
                avg));
        sb.append("Total connections: ").append(connectedDevices).append("\n");
        sb.append("Total routers: ").append(routerCount).append("\n");
        return sb.toString();
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    private String deriveRegion(String houseID) {
        if (houseID == null || !houseID.contains("-")) return "Unknown";
        String prefix = houseID.split("-")[0].toUpperCase();
        switch (prefix) {
            case "WKO": return "Waikato (WKO)";
            case "AKL": return "Auckland (AKL)";
            case "WLG": return "Wellington (WLG)";
            case "CHC": return "Canterbury (CHC)";
            case "OTG": return "Otago (OTG)";
            case "BOP": return "Bay of Plenty (BOP)";
            case "MWT": return "Manawatu-Whanganui (MWT)";
            case "TAS": return "Tasman (TAS)";
            case "NSN": return "Nelson (NSN)";
            case "HKB": return "Hawke's Bay (HKB)";
            default:    return prefix;
        }
    }
}
