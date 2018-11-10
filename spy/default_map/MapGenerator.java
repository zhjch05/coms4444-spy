package spy.default_map;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import spy.sim.Point;

public class MapGenerator implements spy.sim.MapGenerator {
    public List<Point> waterCells()
    {
        return new ArrayList<Point>();
    }
    public List<Point> muddyCells()
    {
        return new ArrayList<Point>();
    }
    public Point packageLocation()
    {
        return new Point(0, 0);
    }
    public Point targetLocation()
    {
        return new Point(99, 99);
    }
    public List<Point> startingLocations()
    {
        ArrayList<Point> startingLocations = new ArrayList<Point>();
        for (int i = 0; i < 30; i++)
        {
            startingLocations.add(new Point(50, i));
        }
        return startingLocations;
    }
}
