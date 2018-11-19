package spy.g3;

import java.util.List;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;

import spy.sim.Point;
import spy.sim.Record;
import spy.sim.CellStatus;
import spy.sim.Simulator;
import spy.sim.Observation;


public class Player implements spy.sim.Player {
    
    private ArrayList<ArrayList<Record>> records; // 2-dim list of cells on map (personal records)
    private int id;
    private Point loc; // Current location on map

    private ArrayList<ArrayList<Record>> landInfo; // similar to 'records' but global for dry land claims
    private ArrayList<ArrayList<Record>> mudInfo; // similar to 'records' but global for muddy land claims

    private int x_dir = 1;
    private int y_dir = 0;
    private int num_moves = 0;
    private List<Point> water;
    
    public void init(int n, int id, int t, Point startingPos, List<Point> waterCells, boolean isSpy)
    {
        this.id = id;
        this.records = new ArrayList<ArrayList<Record>>();
        for (int i = 0; i < 100; i++)
        {
            ArrayList<Record> row = new ArrayList<Record>();
            for (int j = 0; j < 100; j++)
            {
                row.add(null);
            }
	    System.out.println(row);
            this.records.add(row);
        }

        water = waterCells;
    }
    
    public void observe(Point loc, HashMap<Point, CellStatus> statuses)
    {
        this.loc = loc;
	System.out.println("Called observe function =========");
        for (Map.Entry<Point, CellStatus> entry : statuses.entrySet())
        {
            Point p = entry.getKey();
            CellStatus status = entry.getValue();
            Record record = records.get(p.x).get(p.y);

	    System.out.println(p + " " + status + " " );
            if (record == null || record.getC() != status.getC() || record.getPT() != status.getPT())
            {
                ArrayList<Observation> observations = new ArrayList<Observation>();
                record = new Record(p, status.getC(), status.getPT(), observations);
                records.get(p.x).set(p.y, record);
            }
            record.getObservations().add(new Observation(this.id, Simulator.getElapsedT()));
        }
    }
    
    public List<Record> sendRecords(int id)
    {
        System.out.println("Called sendRecords ======");	  
        ArrayList<Record> toSend = new ArrayList<Record>();
        for (ArrayList<Record> row : records)
        {
            for (Record record : row)
            {
                if (record != null)
                {
                    toSend.add(record);
                }
            }
        }
        return toSend;
    }
    
    public void receiveRecords(int id, List<Record> records)
    {
	System.out.println("Called receiveRecords Command ========");
    }
    
    public List<Point> proposePath()
    {
        return null;
    }
    
    public List<Integer> getVotes(HashMap<Integer, List<Point>> paths)
    {
        for (Map.Entry<Integer, List<Point>> entry : paths.entrySet())
        {
            ArrayList<Integer> toReturn = new ArrayList<Integer>();
            toReturn.add(entry.getKey());
            return toReturn;
        }
        return null;
    }
    
    public void receiveResults(HashMap<Integer, Integer> results)
    {
       	System.out.println("Called receiveResults Command =======");
    }
    
    public List<Point> line(Point a, Point b)
    {
        Integer dx = b.x - a.x;
        Integer dy = b.y - a.y;

        ArrayList<Point> pts = new ArrayList<Point>();

        if (a.x == b.x)
        { 
            if (a.y < b.y)
            {
                for (int i = a.y; i <= b.y; i++)
                {
                    pts.add(new Point(a.x, i));
                }
            }
            else
            {
                for (int i = a.y; i >= b.y; i--)
                {
                    pts.add(new Point(a.x, i));
                }
            }
        }
        else if (a.y == b.y)
        { 
            if (a.x < b.x)
            {
                for (int i = a.x; i <= b.x; i++)
                {
                    pts.add(new Point(i, a.y));
                }
            }
            else
            {
                for (int i = a.x; i >= b.x; i--)
                {
                    pts.add(new Point(i, a.y));
                }
            }
        }

        return pts;
    }

    public Boolean validMove()
    {
        Point move = new Point(loc.x + x_dir, loc.y + y_dir);
        if ((!water.contains(move) && (loc.x >= 2) && (loc.x <= 97) && (loc.y >= 2) && (loc.y <= 97)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public Point getMove()
    {
	    System.out.println("Called getMove Command =======");

        Integer x_prop = loc.x + x_dir;
        Integer y_prop = loc.y + y_dir;
        Point candidate = new Point(x_prop, y_prop); while ((water.contains(candidate) || (x_prop < 2) || (x_prop > 97) || (y_prop < 2) || (y_prop > 97))) { if (x_dir == 1)
            {
                x_dir = 0;
                y_dir = 1;
            }
            else if (y_dir == 1)
            {
                x_dir = -1;
                y_dir = 0;
            }
            else if (x_dir == -1)
            {
                x_dir = 0;
                y_dir = -1;
            }
            else
            {
                x_dir = 1;
                y_dir = 0;
            }
            x_prop = loc.x + x_dir;
            y_prop = loc.y + y_dir;

            candidate = new Point(x_prop, y_prop);
        }

        return new Point(x_dir, y_dir);

        //if ((!water.contains(new Point(loc.x + 1, loc.y))) && (loc.x + 1 <= 97))
        //{
        //    move = new Point(1, 0);
        //}
        //else if ((!water.contains(new Point(loc.x, loc.y + 1))) && (loc.y + 1 <= 97))
        //{
        //    move = new Point(0, 1);
        //}
        //else if ((!water.contains(new Point(loc.x - 1, loc.y))) && (loc.x - 1 >= 2))
        //{
        //    move = new Point(-1, 0);
        //}
        //else if ((!water.contains(new Point(loc.x, loc.y - 1))) && (loc.y - 1 >= 2))
        //{
        //    move = new Point(0, -1);
        //}
        //else
        //{
        //    move = new Point(-1, 0);
        //}

        //ArrayList<Point> points = new ArrayList<Point>();

        //points.add(new Point(3, 4));
        //points.add(new Point(4, 4));
        //points.add(new Point(4, 3));

        //points.add(new Point(3, 96));
        //points.add(new Point(10, 96));
        //points.add(new Point(10, 3));
        //points.add(new Point(17, 3));
        //points.add(new Point(17, 96));
        //points.add(new Point(24, 96));
        //points.add(new Point(24, 3));
        //points.add(new Point(31, 3));
        //points.add(new Point(31, 96));
        //points.add(new Point(38, 96));
        //points.add(new Point(38, 3));
        //points.add(new Point(45, 3));
        //points.add(new Point(45, 96));
        //points.add(new Point(52, 96));
        //points.add(new Point(52, 3));
        //points.add(new Point(59, 3));
        //points.add(new Point(59, 96));
        //points.add(new Point(66, 96));
        //points.add(new Point(66, 3));
        //points.add(new Point(73, 3));
        //points.add(new Point(73, 96));
        //points.add(new Point(80, 96));
        //points.add(new Point(80, 3));
        //points.add(new Point(87, 3));
        //points.add(new Point(87, 96));
        //points.add(new Point(94, 96));
        //points.add(new Point(94, 3));
        //points.add(new Point(96, 3));
        //points.add(new Point(96, 96));

        //ArrayList<Point> path = new ArrayList<Point>();

        //Point a = new Point(3, 3);
        //Point b;

        //for (Point p: points)
        //{
        //    b = p;
        //    List<Point> mini_path = line(a, b);
        //    for (Point p_: mini_path)
        //    {
        //        path.add(p_);
        //    }
        //    a = p;
        //}
 
        //Point dest = path.get(num_moves);
        //System.out.println(dest);
        //num_moves += 1;

        //return move;
    }
}
