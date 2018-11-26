package spy.g6;

import java.util.List;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import spy.sim.Point;
import spy.sim.Record;
import spy.sim.CellStatus;
import spy.sim.Simulator;
import spy.sim.Observation;

public class Player implements spy.sim.Player {
    
    private ArrayList<ArrayList<ArrayList<Record>>> records;
    private int id;
    private Point loc;
    private HashMap<Integer,ArrayList<Record>> recordsToldBy;
    private HashMap<Integer,ArrayList<Point>> pointsToldBy;
    
    private static final int EVANS_CONVENTION = 15;
    
    // Keeping track of when was a specific player last seen by us.
    private int[] lastPlayerSeen;
    
    // Task queues for movements
    private PriorityQueue<MovementTask> tasks;
    
    public void init(int n, int id, int t, Point startingPos, List<Point> waterCells, boolean isSpy)
    {
        this.id = id;
        this.records = new ArrayList<ArrayList<ArrayList<Record>>>(100);
        this.recordsToldBy = new HashMap<>();
        this.pointsToldBy = new HashMap<>();
        
        for (int i = 0; i < 100; i++){
        	ArrayList<ArrayList<Record>> row = new ArrayList<ArrayList<Record>>(100);
            for (int j = 0; j < 100; j++){
                row.set(j, new ArrayList<Record>());
            }
            records.set(i, row);
        }
        
        lastPlayerSeen = new int[n];
        tasks = new PriorityQueue<MovementTask>();
        
        // System.out.println(this.records);
    }
    
    public void observe(Point loc, HashMap<Point, CellStatus> statuses)
    {
        this.loc = loc;
        int time = Simulator.getElapsedT();
      
        for (Map.Entry<Point, CellStatus> entry : statuses.entrySet())
        {
            Point p = entry.getKey();
            CellStatus status = entry.getValue();
            
            List<Record> recList = records.get(p.x).get(p.y);
            Record record = new Record(p, status.getC(), status.getPT(), new ArrayList<Observation>());
            record.getObservations().add(new Observation(this.id, time));
            recList.add(record);
            
            for (Integer player: status.getPresentSoldiers()) {
            	if (lastPlayerSeen[player] + EVANS_CONVENTION < time) {
            		tasks.add(new MeetPlayer(player));
            	}
            }
            /*
            if (record == null || record.getC() != status.getC() || record.getPT() != status.getPT())
            {
                ArrayList<Observation> observations = new ArrayList<Observation>();
                record = new Record(p, status.getC(), status.getPT(), observations);
                records.get(p.x).set(p.y, record);
                
                // Find nearby players

            }*/

        }
        // System.out.println(records);
    }
    
    public List<Record> sendRecords(int id)
    {
        ArrayList<Record> toSend = new ArrayList<Record>();
        for (ArrayList<ArrayList<Record>> row : records)
        	for (ArrayList<Record> cell : row)
        		for (Record record : cell)
        			if (record != null){
        				toSend.add(record);
        			}
        return toSend;
    }
    
    public void receiveRecords(int id, List<Record> records)
    {


        ArrayList<Record> receivedRecs = new ArrayList<Record>();
        ArrayList<Point> receivedPoints = new ArrayList<Point>();

        for (Record record : records)
        {
            //only add the record if not null
            if (record != null)
            {
                receivedRecs.add(record);
                receivedPoints.add(record.getLoc());


            }
        }

        //keep track of records told by a specific player
        if (recordsToldBy.containsKey(id)){
            // concatenate receivedRecs
            recordsToldBy.get(id).addAll(receivedRecs);
        }else{
            recordsToldBy.put(id,receivedRecs);

        }

        //keep track of all the points told by a specific player
        if (pointsToldBy.containsKey(id)){
            // concatenate receivedPoints
            pointsToldBy.get(id).addAll(receivedPoints);
        }else{
            pointsToldBy.put(id,receivedPoints);

        }


        




        
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
            // return entry.getKey();
            return new ArrayList<Integer>(entry.getKey());
        }
        return null;
    }
    
    public void receiveResults(HashMap<Integer, Integer> results)
    {


        
    }
    
    public Point getMove()
    {
        Random rand = new Random();
        int x = rand.nextInt(2) * 2
         - 1;
        int y = rand.nextInt(2 + Math.abs(x)) * (2 - Math.abs(x)) - 1;
        return new Point(x, y);
    }
}
