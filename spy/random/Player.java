package spy.random;

import java.util.List;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.HashMap;

import spy.sim.Point;
import spy.sim.Record;
import spy.sim.CellStatus;
import spy.sim.Simulator;

public class Player implements spy.sim.Player {
    
    private ArrayList<ArrayList<Record>> records;
    private int id;
    
    public void init(int n, int id, int t, Point startingPos, List<Point> waterCells, boolean isSpy)
    {
        this.id = id;
        this.records = new ArrayList<ArrayList<ArrayList<Record>>>();
        for (int i = 0; i < 100; i++)
        {
            ArrayList<ArrayList<Record> row = new ArrayList<ArrayList<Record>>();
            for (int j = 0; j < 100; j++)
            {
                row.add(null);
            }
            this.records.add(entry);
        }
    }
    
    public void observe(HashMap<Point, CellStatus> statuses)
    {
        for (Map.Entry<Point, CellStatus> entry : statuses.entrySet())
        {
            Point loc = entry.getKey();
            CellStatus status = entry.getValue();
            Record record = records.get(loc.x).get(loc.y);
            if (record == null || record.getC() != status.getC() || record.getPT() != status.getPT())
            {
                ArrayList<Observation> observations = new ArrayList<Observation>();
                record = new Record(loc, status.getC(), status.getPT(), observations);
                recods.get(loc.x).set(loc.y, record);
            }
            record.getObservations().add(new Observation(this.id, Simulator.getElapsedT()));
        }
    }
    
    public List<Record> sendRecords(int id)
    {
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
        
    }
    
    public List<Point> proposePath()
    {
        return null;
    }
    
    public int getVote(HashMap<Integer, List<Point>> paths)
    {
        return -1;
    }
    
    public void receiveResults(HashMap<Integer, Integer> results)
    {
        
    }
    
    public Point getMove()
    {
        Random rand = new Random();
        int x = rand.nextInt(2) * 2 - 1;
        int y = rand.nextInt(2 + Math.abs(x)) * (2 - Math.abs(x)) - 1;
        return new Point(x, y);
    }
}
