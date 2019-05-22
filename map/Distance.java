package map;

/**
 * Represents the distance from the origin to the current node
 */
public class Distance implements Comparable<Distance> {

    private Coordinate node;
    private int distance;

    public Distance(Coordinate node, int distance) {
        this.node = node;
        this.distance = distance;
    }

    public Coordinate getNode() {
        return node;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public int compareTo(Distance distance) {
        int thisDist = this.distance;
        int otherDist = distance.getDistance();
        if (thisDist == otherDist) {
            return 0;
        } else if (thisDist > otherDist) {
            return 1;
        } else {
            return -1;
        }
    }

}
