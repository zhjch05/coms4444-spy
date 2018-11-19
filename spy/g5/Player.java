package spy.g5;

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

public class Player implements spy.sim.Player 
{
	// This Matrix has a 100% Truthful Map because
	// I assume we can trust ourselves right?
	// There is no point to use Record because I don't care what other players say!
	private ArrayList<ArrayList<Record>> truth_table;
	
	// This Matrix has a <= 100% Turthful map because
	// we can't trust others.
    private ArrayList<ArrayList<Record>> records;
    
    // Parse Init
    private int id;
    private Point current;
    private boolean isSpy;
    private List<Point> waterCells;
    private int time;
    private final static int SIZE = 100;
    
    // Spy functions
    private boolean spy_detected;
    private int SPY_ID = -1;
    
    
    // Keep Location of Target and Package
    private Point target_loc;
    private Point package_loc;

    public void init(int n, int id, int t, Point startingPos, List<Point> waterCells, boolean isSpy)
    {
        // n = number of players
        this.id = id;// id is our Player id (G5)
        this.time = t; // time out argument
        this.current = startingPos;// Current Position
        this.waterCells = waterCells; //Water, Can't pass them 
        this.isSpy = isSpy;//Am I spying?\
        
        // Initialize Maps
        this.records = new ArrayList<ArrayList<Record>>();
        this.truth_table = new ArrayList<ArrayList<Record>>();
        
        for (int i = 0; i < SIZE; i++)
        {
            ArrayList<Record> row = new ArrayList<Record>();
            ArrayList<Record> true_row = new ArrayList<Record>();
            for (int j = 0; j < SIZE; j++)
            {
                row.add(null);
                true_row.add(null);
            }
            this.records.add(row);
            this.truth_table.add(true_row);
        }
    }
    
    // This is our observation, so we know this is factual.
    // Like do I really need to check if Package mismatched? Or Condition?
    public void observe(Point loc, HashMap<Point, CellStatus> statuses)
    {
    	// Update current solider location
        this.current = loc;

        for (Map.Entry<Point, CellStatus> entry : statuses.entrySet())
        {
            Point p = entry.getKey();
            CellStatus status = entry.getValue();
            List<Integer> players = status.getPresentSoldiers();
            if(players != null)
            {
            	//Oh shit we found at least one other player, lets hit em up?
            	
            }
            
            int condition = status.getC();
            int type = status.getPT();
            if(condition == 0)
            {
            	//Not Mud!
            }
            else if(condition == 1)
            {
            	//Mud!
            }
            
            // Package Found!
            if(type == 1)
            {
                package_loc = loc;
            }
            // Target found
            else if(type == 2)
            {
            	target_loc = loc;
            }
            // 0 just means not special tile...
            
            // Check our entry. Row: x, Column: y at our truth only table
            Record record = truth_table.get(p.x).get(p.y);
            if (record == null)
            {
                ArrayList<Observation> observations = new ArrayList<Observation>();
                record = new Record(p, status.getC(), status.getPT(), observations);
                records.get(p.x).set(p.y, record);
            }
            else
            {
            	// In a truth table, only we add our oberservation ourselves? 
            	// As a spy we may need to start lying here?
            	record.getObservations().add(new Observation(this.id, Simulator.getElapsedT()));
            }
        }
    }
    
    private boolean liar_found()
    {
    	// Comare our truth map with our record table...
    	for(int row = 0; row < SIZE; row++)
    	{
    		 List<Record> truth_row = truth_table.get(row);
             List<Record> current_row = records.get(row);
    	     for (int col = 0; col < SIZE; col++)
             {
    	    	 Record truth = truth_row.get(col);
    	    	 Record challenge = current_row.get(col);
    	    	 if(truth == null)
    	    	 {
    	    		 // Well fuck and unknown unknown?
    	    		 // We can put this as unverifiable?
    	    		 if(challenge != null)
    	    		 {
    	    			 continue;
    	    		 }
    	    	 }
    	    	 // Nothing to test the lie against, so eh w/e
    	    	 if(challenge == null)
    	    	 {
    	    		 continue;
    	    	 }
    	    	 // Somone lied about the Mud/Not Mud! The horror!
    	    	 if(truth.getC() != challenge.getC())
    	    	 {
    	    		 return false;
    	    	 }
    	    	 // Someone lied about package/target location! Oh the humanity!
    	    	 // If there is a bug, we can check if it is swapped by someone?
    	    	 if(truth.getPT() != challenge.getPT())
    	    	 {
    	    		return false; 
    	    	 }
             }
    	}
    	return true;
    }
    
    public List<Record> sendRecords(int id)
    {
        ArrayList<Record> toSend = new ArrayList<Record>();
    	if(isSpy)
    	{
    		// We should lie mwahahaha
    		
    	}
    	else
    	{
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
    	}
        return toSend;
    }
    
    // Place this in our Record Table, can contain lies!
    // Another idea is if we are sure one ID is a soy, we can just ignore...
    public void receiveRecords(int id, List<Record> records)
    {
    	if(id == SPY_ID)
    	{
    		// Yeah fuck this I am not trusting a spy...
    		// But should I listen to one and keep the data?
    	}
    	else
    	{
    		for(int i = 0; i < records.size(); i++)
    		{
    			Record r = records.get(i);
    			if(is_lying(r) == 1)
    			{
    				// BLOCK EVERYYTHING!
    				// SPY IS FOUND!
    				SPY_ID = id;
    			}
    		}
    		
    		// Append to current observations? 
    		// Check contradicting claims?
    		
    		// For now, just copy it to records?
    	}
    }
    
    private int is_lying(Record unknown)
    {
    	Record truth = truth_table.get(unknown.getLoc().x).get(unknown.getLoc().y);
    	if(truth == null)
    	{
    		// Nothing can be done to check, argh!
    		return -1;
    	}
    	else
    	{
    		// Lie detected
    		if(truth.getC() != unknown.getC())
    		{
    			return 1;
    		}
    		// Lie detected
    		if(truth.getPT() != unknown.getPT())
    		{
    			return 1;
    		}
    		// No lie detected
    		return 0;
    	}
    }
    
    public List<Point> proposePath()
    {
    	if(isSpy)
    	{
    		return null;
    	}
    	else
    	{
    		MazeSolver solution = new MazeSolver(package_loc, target_loc, truth_table);
    		solution.solve();
    		return solution.path;
    	}
    }
    
    public List<Integer> getVotes(HashMap<Integer, List<Point>> paths)
    {
        for (Map.Entry<Integer, List<Point>> entry : paths.entrySet())
        {
            ArrayList<Integer> toReturn = new ArrayList<Integer>();
            toReturn.add(entry.getKey());
            return new ArrayList<Integer>(entry.getKey());
        }
        return null;
    }
    
    public void receiveResults(HashMap<Integer, Integer> results)
    {
        
    }
    
    // Move to next location
    public Point getMove()
    {
        Random rand = new Random();
        int x = rand.nextInt(2) * 2 - 1;
        int y = rand.nextInt(2 + Math.abs(x)) * (2 - Math.abs(x)) - 1;
        return new Point(x, y);
    }
}
