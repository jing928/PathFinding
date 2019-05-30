package map;

/**
 * Represents an edge/path between two {@link Coordinate}s on the map
 */
public class Edge implements Comparable<Edge> {

    private Coordinate from; // The start Node of the Edge
    private Coordinate to; // The end Node of the Edge
    private int weight; // The weight/distance or cost or traveling through the Edge

    public Edge(Coordinate from, Coordinate to) {
        this(from, to, 1);
    }
    public Edge(Coordinate from, Coordinate to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Coordinate getFrom() {
        return from;
    }

    public Coordinate getTo() {
        return to;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public int compareTo(Edge edge) {
        int thisWeight = weight;
        int otherWeight = edge.getWeight();
        if (thisWeight == otherWeight) {
            return 0;
        } else if (thisWeight > otherWeight) {
            return 1;
        } else {
            return -1;
        }
    }

}
