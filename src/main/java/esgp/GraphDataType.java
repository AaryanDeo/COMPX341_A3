package esgp;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.ArrayList;

/**
 * GraphDataType wraps the GraphStream library to build and display a
 * graph of Encost smart home devices, as specified in the SDS (section 4.4).
 *
 * <p>Nodes represent devices. Edges represent router connections.
 * Devices are visually distinguished by:
 * <ul>
 *   <li><b>Colour</b> — based on device category (SDS section 2.1.8)</li>
 *   <li><b>Shape</b> — based on send/receive capability (SDS wireframe 4.7.5):
 *       Square=Send only, Circle=Send+Receive, Triangle=Receive only</li>
 * </ul>
 */
public class GraphDataType {

    /** The underlying GraphStream graph object. */
    private Graph graph = new SingleGraph("ESGP");

    /** The list of Device objects from the dataset. */
    private ArrayList<Device> devices;

    // -----------------------------------------------------------------------
    // Public API
    // -----------------------------------------------------------------------

    /**
     * Sets the device list that this graph will represent.
     *
     * @param devices the ArrayList of {@link Device} objects from the dataset
     */
    public void setDevices(ArrayList<Device> devices) {
        this.devices = devices;
    }

    /**
     * Adds a node to the graph representing the given device.
     * Sets visual attributes: label, colour (by category), and
     * shape (by send/receive capability).
     *
     * @param node the {@link Device} to add as a graph node
     */
    public void addNode(Device node) {
        if (graph.getNode(node.getDeviceID()) != null) return; // already present

        Node graphNode = graph.addNode(node.getDeviceID());
        graphNode.setAttribute("ui.label", node.getName());
        graphNode.setAttribute("category",  node.getCategory());
        graphNode.setAttribute("houseID",   node.getHouseID());

        // Visual styling: colour for category, shape for send/receive
        String style = "fill-color: " + node.getCategoryColour() + "; "
                     + "shape: "      + node.getNodeShape()      + "; "
                     + "size: 20px; "
                     + "text-size: 10;";
        graphNode.setAttribute("ui.style", style);
    }

    /**
     * Adds a directed edge representing the router connection between two devices.
     *
     * @param routerConnection the device ID of the parent router/extender
     * @param node1            the device ID of the parent node
     * @param node2            the device ID of the child node
     */
    public void addEdge(String routerConnection, String node1, String node2) {
        String edgeId = node1 + "--" + node2;
        if (graph.getEdge(edgeId) != null) return; // already present
        if (graph.getNode(node1) == null || graph.getNode(node2) == null) return;
        graph.addEdge(edgeId, node1, node2, true); // directed edge
    }

    /**
     * Removes the node representing the given device from the graph.
     *
     * @param node the {@link Device} to remove
     */
    public void removeNode(Device node) {
        graph.removeNode(node.getDeviceID());
    }

    /**
     * Removes the edge identified by the given router connection string.
     *
     * @param routerConnection the edge identifier to remove
     */
    public void removeEdge(String routerConnection) {
        graph.removeEdge(routerConnection);
    }

    /**
     * Builds the complete graph from the current device list and displays
     * it in a UI window via GraphStream.
     */
    public void displayGraph() {
        graph = new SingleGraph("ESGP");
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        // Pass 1: add all nodes
        for (Device d : devices) {
            addNode(d);
        }

        // Pass 2: add all edges
        for (Device d : devices) {
            if (!d.isRootDevice()) {
                addEdge(d.getRouterConnection(),
                        d.getRouterConnection(),
                        d.getDeviceID());
            }
        }

        graph.display();
    }

    /**
     * Returns the underlying GraphStream {@link Graph} object.
     * Useful for testing without opening a UI window.
     */
    public Graph getGraph() {
        return graph;
    }
}
