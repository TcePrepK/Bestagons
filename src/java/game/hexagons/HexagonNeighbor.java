package game.hexagons;

import toolbox.Points.Point2D;

public class HexagonNeighbor {
    public static final int neighborAmount = 6;

    static public Point2D[] getNeighbors(final int y) {
        final Point2D[] neighbors = new Point2D[HexagonNeighbor.neighborAmount];

        neighbors[0] = new Point2D(1, 0);
        neighbors[1] = new Point2D((y % 2), 1);
        neighbors[2] = new Point2D((y % 2) - 1, 1);
        neighbors[3] = new Point2D(-1, 0);
        neighbors[4] = new Point2D((y % 2) - 1, -1);
        neighbors[5] = new Point2D((y % 2), -1);

        return neighbors;
    }
}
