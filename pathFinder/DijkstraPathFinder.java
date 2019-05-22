package pathFinder;

import map.*;

import java.util.*;

public class DijkstraPathFinder implements PathFinder {

    // Graph representation of the map
    private Graph graph;
    private int coordinatesExploredCounter = 0;
    private Map<Coordinate, Edge> distances;
    private PriorityQueue<Edge> minDistQueue;
    private Map<Coordinate, Edge> settledNodes;
    // List of origin coordinates
    private List<Coordinate> origins;
    // list of destination coordinates
    private List<Coordinate> destinations;
    // list of waypoint coordinates
    private List<Coordinate> waypoints;

    public DijkstraPathFinder(PathMap map) {
        origins = map.originCells;
        destinations = map.destCells;
        waypoints = map.waypointCells;
        settledNodes = new HashMap<>();
        graph = new Graph(map);
        graph.initGraph();
    } // end of DijkstraPathFinder()

    @Override
    public List<Coordinate> findPath() {
        initDistances();
        initQueue();
        while (!minDistQueue.isEmpty()) {
            Edge minDist = minDistQueue.remove();
            Coordinate node = minDist.getTo();
            if (!settledNodes.containsKey(node)) {
                Edge edge;
                Coordinate from = minDist.getFrom();
                if (from == null) {
                    edge = new Edge(node, node, minDist.getWeight());
                } else {
                    edge = new Edge(from, node, minDist.getWeight());
                }
                settledNodes.put(node, edge);
                coordinatesExploredCounter++;
                updateNeighbors(node);
            }
        }
        return getShortestPath();
    } // end of findPath()

    private List<Coordinate> getShortestPath() {
        LinkedList<Coordinate> path = new LinkedList<>();
        List<Edge> distToDest = new ArrayList<>();
        for (Coordinate dest : destinations) {
            distToDest.add(settledNodes.get(dest));
        }
        Edge shortestDistToDest = Collections.min(distToDest);
        boolean originFound = false;
        Coordinate prevNode = shortestDistToDest.getTo();
        while (!originFound) {
            path.addFirst(prevNode);
            if (isOrigin(prevNode)) {
                originFound = true;
            } else {
                Edge edge = settledNodes.get(prevNode);
                prevNode = edge.getFrom();
            }
        }
        return path;
    }

    private void updateNeighbors(Coordinate node) {
        List<Edge> neighborEdges = graph.getNeighbors(node);
        for (Edge neighborEdge : neighborEdges) {
            Coordinate neighborNode = neighborEdge.getTo();
            int currentDistValue = distances.get(neighborNode).getWeight();
            int newDistValue = distances.get(node).getWeight() + neighborEdge.getWeight();
            if (newDistValue < currentDistValue) {
                Edge newDist = new Edge(node, neighborNode, newDistValue);
                distances.put(neighborNode, newDist);
                minDistQueue.offer(newDist);
            }
        }
    }

    /**
     * Initialize distances from origin to each node
     */
    private void initDistances() {
        distances = new HashMap<>();
        for (Coordinate origin : origins) {
            distances.put(origin, new Edge(origin, origin, 0));
        }

        for (Coordinate node : graph.getNodes()) {
            if (!isOrigin(node)) {
                distances.put(node, new Edge(null, node, Integer.MAX_VALUE));
            }
        }
    }

    private void initQueue() {
        minDistQueue = new PriorityQueue<>();
        for (Coordinate origin : origins) {
            minDistQueue.add(distances.get(origin));
        }
    }

    private boolean isOrigin(Coordinate node) {
        return origins.contains(node);
    }

    @Override
    public int coordinatesExplored() {
        return coordinatesExploredCounter;
    } // end of cellsExplored()

} // end of class DijsktraPathFinder
