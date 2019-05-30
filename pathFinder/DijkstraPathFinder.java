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
    private List<List<Coordinate>> shortestPaths;
    private int totalCostOfCurrentPath;
    private Map<Integer, Integer> distToIndex;
    private PriorityQueue<Integer> shortestPathQueue;

    public DijkstraPathFinder(PathMap map) {
        origins = map.originCells;
        destinations = map.destCells;
        waypoints = map.waypointCells;
        graph = new Graph(map);
        graph.initGraph();
    } // end of DijkstraPathFinder()

    @Override
    public List<Coordinate> findPath() {
        totalCostOfCurrentPath = 0;
        if (waypoints.isEmpty()) {
            findPath(origins, destinations);
            System.out.println("Total cost: " + totalCostOfCurrentPath);
            return shortestPath;
        } else {
            findPathWithWayPoints();
            int shortestCost = shortestPathQueue.poll();
            System.out.println("Total cost: " + shortestCost);
            return shortestPaths.get(distToIndex.get(shortestCost));
        }
    } // end of findPath()

    private void findPathWithWayPoints() {
        shortestPaths = new ArrayList<>();
        distToIndex = new HashMap<>();
        shortestPathQueue = new PriorityQueue<>();
        shortestPathQueue.add(Integer.MAX_VALUE);
        findPathForEachPermutation(waypoints.size());
    }

    private void calculatePath(List<Coordinate> waypointsOrder) {
        for (int i = 0; i < origins.size(); i++) {
            List<Coordinate> waypointsOrderCopy = new ArrayList<>(waypointsOrder);
            List<Coordinate> origin = origins.subList(i, i + 1);
            List<Coordinate> lastDest = origin;
            while (!waypointsOrderCopy.isEmpty()) {
                List<Coordinate> dest = new ArrayList<>();
                dest.add(waypointsOrderCopy.remove(0));
                lastDest = findPath(lastDest, dest);
            }
            findPath(lastDest, destinations);
            if (totalCostOfCurrentPath < shortestPathQueue.peek().intValue()) {
                shortestPathQueue.remove();
                shortestPathQueue.add(totalCostOfCurrentPath);
                distToIndex.put(totalCostOfCurrentPath, shortestPaths.size());
                shortestPaths.add(shortestPath);
            }
            totalCostOfCurrentPath = 0;
            shortestPath = null;
        }
    }

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
        Edge shortestDistToDest = new Edge(null, null);
        for (Coordinate dest : destinations) {
            if (settledNodes.containsKey(dest)) {
                shortestDistToDest = settledNodes.get(dest);
                break;
            }
        }
        boolean originFound = false;
        Coordinate prevNode = shortestDistToDest.getTo();

        while (!originFound) {
            path.addFirst(prevNode);
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

    private void mergePaths(LinkedList<Coordinate> path) {
        if (shortestPath == null) {
            shortestPath = path;
            totalCostOfCurrentPath -= shortestPath.getFirst().getTerrainCost();
        } else {
            Coordinate lastNode = shortestPath.removeLast();
            totalCostOfCurrentPath -= lastNode.getTerrainCost();
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
