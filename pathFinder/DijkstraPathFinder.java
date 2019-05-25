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
    private LinkedList<Coordinate> shortestPath;

    public DijkstraPathFinder(PathMap map) {
        origins = map.originCells;
        destinations = map.destCells;
        waypoints = map.waypointCells;
        graph = new Graph(map);
        graph.initGraph();
    } // end of DijkstraPathFinder()

    @Override
    public List<Coordinate> findPath() {
        if (waypoints.isEmpty()) {
            findPath(origins, destinations);
        } else {
            List<Coordinate> lastDest = findPath(origins, waypoints);
            while (!waypoints.isEmpty()) {
                lastDest = findPath(lastDest, waypoints);
            }
            findPath(lastDest, destinations);
        }
        System.out.println(shortestPath.size());
        return shortestPath;
    } // end of findPath()

    private List<Coordinate> findPath(List<Coordinate> origins, List<Coordinate> destinations) {
        initDistances(origins);
        initQueue(origins);
        initSettledNodes();
        boolean destFound = false;
        while (!minDistQueue.isEmpty() && !destFound) {
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
                if (destinations.contains(node)) {
                    destFound = true;
                } else {
                    updateNeighbors(node);
                }
            }
        }
        updateShortestPath(origins, destinations);
        List<Coordinate> lastDest = new ArrayList<>();
        lastDest.add(shortestPath.getLast());
        return lastDest;
    }

    private void updateShortestPath(List<Coordinate> origins, List<Coordinate> destinations) {
        LinkedList<Coordinate> path = new LinkedList<>();
        List<Edge> distToDest = new ArrayList<>();
        for (Coordinate dest : destinations) {
            if (settledNodes.containsKey(dest)) {
                distToDest.add(settledNodes.get(dest));
                // TODO: should break here
            }
        }
        Edge shortestDistToDest = Collections.min(distToDest); // TODO: Possibly remove
        boolean originFound = false;
        Coordinate prevNode = shortestDistToDest.getTo();
        // Remove the found destination from the list
        destinations.remove(prevNode);

        while (!originFound) {
            path.addFirst(prevNode);
            if (origins.contains(prevNode)) {
                originFound = true;
            } else {
                Edge edge = settledNodes.get(prevNode);
                prevNode = edge.getFrom();
            }
        }
        mergePaths(path);
    }

    private void mergePaths(LinkedList<Coordinate> path) {
        if (shortestPath == null) {
            shortestPath = path;
        } else {
            shortestPath.removeLast();
            shortestPath.addAll(path);
        }
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
    private void initDistances(List<Coordinate> origins) {
        distances = new HashMap<>();
        for (Coordinate origin : origins) {
            distances.put(origin, new Edge(origin, origin, 0));
        }

        for (Coordinate node : graph.getNodes()) {
            if (!origins.contains(node)) {
                distances.put(node, new Edge(null, node, Integer.MAX_VALUE));
            }
        }
    }

    private void initQueue(List<Coordinate> origins) {
        minDistQueue = new PriorityQueue<>();
        for (Coordinate origin : origins) {
            minDistQueue.add(distances.get(origin));
        }
    }

    private void initSettledNodes() {
        settledNodes = new HashMap<>();
    }

    @Override
    public int coordinatesExplored() {
        return coordinatesExploredCounter;
    } // end of cellsExplored()

} // end of class DijsktraPathFinder
