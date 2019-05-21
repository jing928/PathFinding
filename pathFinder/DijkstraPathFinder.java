package pathFinder;

import map.Coordinate;
import map.Graph;
import map.PathMap;

import java.util.*;

public class DijkstraPathFinder implements PathFinder {

    // Graph representation of the map
    private Graph graph;
    private int coordinatesExploredCounter = 0;

    public DijkstraPathFinder(PathMap map) {
        graph = new Graph(map);
        graph.initGraph();
    } // end of DijkstraPathFinder()

    @Override
    public List<Coordinate> findPath() {
        // You can replace this with your favourite list, but note it must be a
        // list type
        List<Coordinate> path = new ArrayList<Coordinate>();

        // TODO: Implement

        return path;
    } // end of findPath()


    @Override
    public int coordinatesExplored() {
        return coordinatesExploredCounter;
    } // end of cellsExplored()

} // end of class DijsktraPathFinder
