package map;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * The adjacency list representation of the path map.
 */
public class Graph {

    private PathMap map;
    private Map<Coordinate, LinkedList<Edge>> nodeList;

    public Graph(PathMap map) {
        this.map = map;
        nodeList = new HashMap<>();
    }

    /**
     * Create graph from the PathMap
     */
    public void initGraph() {
        for (int r = 0; r < map.sizeR; r++) {
            for (int c = 0; c < map.sizeC; c++) {
                initNode(r, c);
            }
        }
    }

    /**
     * Initialize a node and it out-neighbors
     * @param r row number
     * @param c column number
     */
    private void initNode(int r, int c) {
        Coordinate node = map.getCell(r, c);
        if (node.getImpassable()) {
            return;
        }
        LinkedList<Edge> edges = new LinkedList<>();
        // Add passable up neighbor
        if (map.isPassable(r + 1, c)) {
            Coordinate up = map.getCell(r + 1, c);
            edges.add(new Edge(node, up, up.getTerrainCost()));
        }
        // Add passable down neighbor
        if (map.isPassable(r - 1, c)) {
            Coordinate down = map.getCell(r - 1, c);
            edges.add(new Edge(node, down, down.getTerrainCost()));
        }
        // Add passable left neighbor
        if (map.isPassable(r, c - 1)) {
            Coordinate left = map.getCell(r, c - 1);
            edges.add(new Edge(node, left, left.getTerrainCost()));
        }
        // Add passable right neighbor
        if (map.isPassable(r, c + 1)) {
            Coordinate right = map.getCell(r, c + 1);
            edges.add(new Edge(node, right, right.getTerrainCost()));
        }
        if (edges.isEmpty()) {
            nodeList.put(node, null);
        } else {
            nodeList.put(node, edges);
        }
    }

    /**
     * Get all nodes of the graph
     * @return a set of all the nodes in the graph
     */
    public Set<Coordinate> getNodes() {
        return nodeList.keySet();
    }

    /**
     * Get all the neighboring {@link Edge}s of a given node
     * @param node a given node ({@link Coordinate}
     * @return a {@link LinkedList} of neighboring {@link Edge}s
     */
    public LinkedList<Edge> getNeighbors(Coordinate node) {
        return nodeList.get(node);
    }

}
