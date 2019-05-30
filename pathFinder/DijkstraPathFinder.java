package pathFinder;

import map.*;

import java.util.*;

public class DijkstraPathFinder implements PathFinder {

    // Graph representation of the map
    private Graph graph;
    private int coordinatesExploredCounter = 0;
    // Keep track of distances to the origins for all the nodes
    private Map<Coordinate, Edge> distances;
    // Store the distances of the neighboring nodes
    private PriorityQueue<Edge> minDistQueue;
    // Store the visited nodes
    private Map<Coordinate, Edge> settledNodes;
    // List of origin coordinates
    private List<Coordinate> origins;
    // list of destination coordinates
    private List<Coordinate> destinations;
    // list of waypoint coordinates
    private List<Coordinate> waypoints;
    // List of Coordinates that are on the shortest path
    private LinkedList<Coordinate> shortestPath;
    // The cost of traveling through the current found path
    private int totalCostOfCurrentPath;
    // The current minimum cost of a path that goes through all waypoints
    private int minCost;
    // The current best path that goes through all waypoints
    private LinkedList<Coordinate> currentBestPath;

    /**
     * Initialize the custom {@link Graph} with a {@link PathMap} instance
     *
     * @param map a grid representation of the graph
     */
    public DijkstraPathFinder(PathMap map) {
        origins = map.originCells;
        destinations = map.destCells;
        waypoints = map.waypointCells;
        graph = new Graph(map);
        graph.initGraph();
    } // end of DijkstraPathFinder()

    /**
     * Find shortest path that meets the parameter requirements
     *
     * @return the list of coordinates that form the shortest path
     */
    @Override
    public List<Coordinate> findPath() {
        totalCostOfCurrentPath = 0;
        if (waypoints.isEmpty()) { // Run Dijkstra directly when there is no waypoints involved
            findPath(origins, destinations);
            System.out.println("Total cost: " + totalCostOfCurrentPath);
            return shortestPath;
        } else { // Run a special methods to find the shortest path when there are waypoints
            findPathWithWaypoints();
            if (minCost < Integer.MAX_VALUE) {
                System.out.println("Total cost: " + minCost);
            }
            return currentBestPath;
        }
    } // end of findPath()

    /**
     * Main method for finding shortest path with waypoints
     * using brute force method by comparing all permutations of
     * waypoint visiting order
     */
    private void findPathWithWaypoints() {
        minCost = Integer.MAX_VALUE; // Set initial minCost to "infinity"
        currentBestPath = new LinkedList<>();
        findPathForEachPermutation(waypoints.size()); // Run helper method to get all permutations
    }

    /**
     * Use the Heap's algorithm to recursively generate all permutations
     * of the list of waypoints to get the visiting order of those waypoints.
     * Instead of saving all the permutations, this method calls {@link DijkstraPathFinder#calculatePath(List)}
     * to find a path that goes through those waypoints in the generated order. This way it
     * significantly reduces the space complexity.
     *
     * @param n the size of the list to generate permutations for. In this case, it's the number of
     *          waypoints
     */
    private void findPathForEachPermutation(int n) {
        if (n == 1) {
            calculatePath(new ArrayList<>(waypoints));
        } else {
            for (int i = 0; i < n; i++) {
                findPathForEachPermutation(n - 1);
                if (n % 2 == 1) {
                    Collections.swap(waypoints, 0, n - 1);
                } else {
                    Collections.swap(waypoints, i, n - 1);
                }
            }
        }
    }

    /**
     * For each waypoints order permutation, it finds N possible paths
     * where N is the number of origin nodes. After each iteration, it compares
     * the current total cost ({@link DijkstraPathFinder#totalCostOfCurrentPath}) to the
     * previously saved minimum cost ({@link DijkstraPathFinder#minCost}).
     * If current cost is lower then update the {@link DijkstraPathFinder#minCost}, otherwise
     * discard the current path as it will not be the shortest.
     * If a path cannot be found, discard the path as well.
     *
     * @param waypointsOrder
     */
    private void calculatePath(List<Coordinate> waypointsOrder) {
        // For every origin in the list of origins
        for (int i = 0; i < origins.size(); i++) {
            List<Coordinate> waypointsOrderCopy = new ArrayList<>(waypointsOrder);
            List<Coordinate> origin = origins.subList(i, i + 1); // Get one origin
            List<Coordinate> lastDest = origin;
            // Find shortest path between segment of the overall path
            while (!waypointsOrderCopy.isEmpty()) {
                List<Coordinate> dest = new ArrayList<>();
                dest.add(waypointsOrderCopy.remove(0));
                lastDest = findPath(lastDest, dest);
                // TODO: waypoint might be inaccessible
            }
            if (!findPath(lastDest, destinations).isEmpty()) {
                if (totalCostOfCurrentPath < minCost) {
                    minCost = totalCostOfCurrentPath;
                    currentBestPath = shortestPath;
                }
            }
            // Reset for the next run
            totalCostOfCurrentPath = 0;
            shortestPath = null;
        }
    }

    /**
     * A generic Dijkstra's algorithm for finding the shortest path between origins and destinations
     *
     * @param origins      a list or origins
     * @param destinations a list of destinations
     * @return the destination of the found shortest path wrapped in a size-1 list
     * or an empty list when no path found
     */
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
        if (shortestPath != null) {
            lastDest.add(shortestPath.getLast());
        }
        return lastDest;
    }

    /**
     * Update the distance to the origin for all the neighbor nodes of a given node as
     * part of the Dijkstra's algorithm.
     *
     * @param node
     */
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
     * Given the origins and destinations, it will backtrace the path from the destination
     * to the origin to get all coordinates on the path.
     *
     * @param origins
     * @param destinations
     */
    private void updateShortestPath(List<Coordinate> origins, List<Coordinate> destinations) {
        LinkedList<Coordinate> path = new LinkedList<>();
        Edge shortestDistToDest = new Edge(null, null);
        for (Coordinate dest : destinations) {
            if (settledNodes.containsKey(dest)) {
                shortestDistToDest = settledNodes.get(dest);
                break;
            }
        }
        boolean originFound = false;
        Coordinate prevNode = shortestDistToDest.getTo();
        if (prevNode == null) {
            shortestPath = null;
            return;
        }

        while (!originFound) {
            path.addFirst(prevNode);
            // Increase the total cost
            totalCostOfCurrentPath += prevNode.getTerrainCost();
            if (origins.contains(prevNode)) {
                originFound = true;
            } else {
                Edge edge = settledNodes.get(prevNode);
                prevNode = edge.getFrom();
            }
        }
        mergePaths(path);
    }

    /**
     * When the final path involves several segments (the waypoint scenario), it will
     * merge the lists coordinates of all the segments together to form the overall path.
     *
     * @param path a new path to be merged
     */
    private void mergePaths(LinkedList<Coordinate> path) {
        /**
         * When the path is the first path, just assign it to the {@link DijkstraPathFinder#shortestPath}
         * otherwise, add it to the existing {@link DijkstraPathFinder#shortestPath}
         */
        if (shortestPath == null) {
            shortestPath = path;
            // Subtract the cost of the origin node
            totalCostOfCurrentPath -= shortestPath.getFirst().getTerrainCost();
        } else {
            Coordinate lastNode = shortestPath.removeLast();
            // Subtract duplicate intermediate destination cost
            totalCostOfCurrentPath -= lastNode.getTerrainCost();
            shortestPath.addAll(path);
        }
    }

    /**
     * Initialize distances from origin to each node.
     * All origins have a distance of 0 and the rest "infinity"
     *
     * @param origins
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

    /**
     * Initialize the priority queue to get the next-to-visit node.
     * At first, only origins are added to the queue.
     *
     * @param origins
     */
    private void initQueue(List<Coordinate> origins) {
        minDistQueue = new PriorityQueue<>();
        for (Coordinate origin : origins) {
            minDistQueue.add(distances.get(origin));
        }
    }

    /**
     * Initialize the settled nodes to empty.
     */
    private void initSettledNodes() {
        settledNodes = new HashMap<>();
    }

    @Override
    public int coordinatesExplored() {
        return coordinatesExploredCounter;
    } // end of cellsExplored()

} // end of class DijsktraPathFinder
