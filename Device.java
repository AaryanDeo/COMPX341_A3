package esgp;

/**
 * Device stores data for a single smart home device parsed from one
 * line of a dataset file, as specified in the SDS (section 4.5).
 *
 * The constructor accepts a raw CSV data line and parses all fields,
 * also determining the device category and visual attributes.
 *
 * Expected CSV line format:
 *   deviceID,dateConnected,deviceName,deviceType,householdID,routerConnection,sends,receives
 */
public class Device {

    private String  deviceID;
    private String  dateConnected;
    private String  name;
    private String  deviceType;
    private String  houseID;
    private String  routerConnection;
    private boolean sends;
    private boolean receives;
    private String  category;

    /**
     * Constructs a Device by parsing a raw CSV data line.
     *
     * @param dataLine one line from a dataset CSV file (not the header)
     * @throws IllegalArgumentException if the line does not have 8 comma-separated fields
     */
    public Device(String dataLine) {
        String[] fields = dataLine.split(",", -1);
        if (fields.length != 8) {
            throw new IllegalArgumentException(
                    "Invalid data line: expected 8 fields but got "
                    + fields.length + " in: " + dataLine);
        }
        this.deviceID         = fields[0].trim();
        this.dateConnected    = fields[1].trim();
        this.name             = fields[2].trim();
        this.deviceType       = fields[3].trim();
        this.houseID          = fields[4].trim();
        this.routerConnection = fields[5].trim();
        this.sends            = "Yes".equalsIgnoreCase(fields[6].trim());
        this.receives         = "Yes".equalsIgnoreCase(fields[7].trim());
        this.category         = determineCategory(this.deviceType);
    }

    // -----------------------------------------------------------------------
    // Getters
    // -----------------------------------------------------------------------

    public String  getDeviceID()         { return deviceID; }
    public String  getDateConnected()    { return dateConnected; }
    public String  getName()             { return name; }
    public String  getDeviceType()       { return deviceType; }
    public String  getHouseID()          { return houseID; }
    public String  getRouterConnection() { return routerConnection; }
    public boolean getSends()            { return sends; }
    public boolean getReceives()         { return receives; }
    public String  getCategory()         { return category; }

    /** Returns true if this device is a root node (no parent router). */
    public boolean isRootDevice() {
        return "-".equals(routerConnection.trim());
    }

    // -----------------------------------------------------------------------
    // Category determination (SDS section 1.4)
    // -----------------------------------------------------------------------

    private String determineCategory(String deviceType) {
        if (deviceType == null) return "Other";
        String t = deviceType.trim().toLowerCase();

        if (t.equals("router") || t.equals("extender"))
            return "Wifi Routers";
        if (t.contains("hub") || t.contains("controller"))
            return "Hubs/Controllers";
        if (t.contains("light") || t.contains("bulb") || t.contains("lighting"))
            return "Smart Lighting";
        if (t.equals("kettle") || t.equals("toaster") || t.equals("coffee maker")
                || t.equals("refrigerator/freezer") || t.contains("appliance"))
            return "Smart Appliances";
        if (t.contains("washing") || t.contains("dryer")
                || t.contains("dishwasher") || t.contains("whiteware"))
            return "Smart Whiteware";
        return "Other";
    }

    // -----------------------------------------------------------------------
    // Visual helpers (SDS section 2.1.8 and wireframe 4.7.5)
    // -----------------------------------------------------------------------

    /**
     * Returns the GraphStream CSS colour for this device's category.
     * Used to visually distinguish categories in the graph.
     */
    public String getCategoryColour() {
        switch (category) {
            case "Wifi Routers":     return "#4A90D9"; // blue
            case "Hubs/Controllers": return "#7B68EE"; // purple
            case "Smart Lighting":   return "#F5A623"; // amber
            case "Smart Appliances": return "#7ED321"; // green
            case "Smart Whiteware":  return "#50E3C2"; // teal
            default:                 return "#9B9B9B"; // grey
        }
    }

    /**
     * Returns the GraphStream CSS shape based on send/receive capability.
     * Per SDS wireframe (section 4.7.5):
     *   Square (box)   = sends only
     *   Circle         = sends and receives
     *   Triangle       = receives only
     */
    public String getNodeShape() {
        if (sends && receives) return "circle";
        if (sends)             return "box";
        if (receives)          return "triangle";
        return "circle";
    }

    @Override
    public String toString() {
        return "Device{id='" + deviceID + "', name='" + name
                + "', type='" + deviceType + "', category='" + category + "'}";
    }
}
