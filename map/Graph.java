package map;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * The adjacency list representation of the path map.
 */
public class Graph {

    private Map<Coordinate, LinkedList<Edge>> nodeList;

    public Graph() {
        nodeList = new HashMap<>();
    }

}
