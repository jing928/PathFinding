package map;

/**
 * Represents an edge between two {@link Coordinate}s on the map
 */
public class Edge implements Comparable<Edge> {

    private Coordinate from;
    private Coordinate to;
    private int weight;

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

    public void setFrom(Coordinate from) {
        this.from = from;
    }

    public Coordinate getTo() {
        return to;
    }

    public void setTo(Coordinate to) {
        this.to = to;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
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
