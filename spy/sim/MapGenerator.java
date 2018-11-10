package spy.sim;

import java.util.HashMap;
import java.util.List;

import spy.sim.Point;

public interface MapGenerator {
    public List<Point> waterCells();
    public List<Point> muddyCells();
    public Point packageLocation();
    public Point targetLocation();
    public List<Point> startingLocations();
}
