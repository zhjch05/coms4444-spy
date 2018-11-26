package spy.g5;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import spy.sim.Point;
import spy.sim.Record;
import spy.sim.CellStatus;
import spy.sim.Simulator;
import spy.sim.Observation;

public class MazeSolver
{
	private static int SIZE = 100;
	private ArrayList<ArrayList<Record>> maze;
	
	private Point start;
	private Point end;
	public List<Point> path = null;
	private List<List<Point>> parents;
    private List<List<Integer>> offsets;

    
    public List<Point> sweep(Point s, ArrayList<ArrayList<Record>> map) { //returns path to nearest unexplored square within mud

    	List<List<Point>> prev = new ArrayList<List<Point>>();

	
    	for(int i=0; i<100; i++)
    	{
	    
    		List row = new ArrayList<Point>();
    		for (int j=0; j<100; j++)
    		{
    			row.add(null);
    		}
    		prev.add(row);
    	}

	
	Point current = null;
        List<Point> explore = new ArrayList<Point>();
        explore.add(s);
	
        while (current == null || map.get(current.x).get(current.y) != null)
        {
        	if(explore.size() == 0) 
        	{
        		return null; //no path is known
        	}
        	else 
        	{
        		current = explore.get(0);
        		explore.remove(explore.get(0));   
        	}
	    
        	for(List<Integer> offset: offsets)
        	{
        		// if cell is in grid, not mud or water and parent is not set
        		Point adjacent = new Point(current.x+offset.get(0), current.y+offset.get(1));
        		if(adjacent.x >=0 && adjacent.x <= 100 && adjacent.y >=0 && adjacent.y <= 100) 
        		{
        			if(prev.get(adjacent.x).get(adjacent.y) == null)
        			{
				    if (map.get(adjacent.x).get(adjacent.y) == null || map.get(adjacent.x).get(adjacent.y).getC() == 0)
        				{
        					// set parent as current
        					// append cell to explore list (unless cell == end, then add to front of list)
        					prev.get(adjacent.x).set(adjacent.y, new Point( current.x, current.y ));
        					explore.add(adjacent);
        				}
        			}
        		}
        	}
        }
	
	
        List<Point> p = new ArrayList<Point>();
        while(current != s) 
        {
        	p.add(current);
        	current = prev.get(current.x).get(current.y);
        }
        p.add(s);
        Collections.reverse(p);	
     
	return p;
    }

    
    public List<Point> explore(Point s, ArrayList<ArrayList<Record>> map) { //returns path to nearest unexplored square

    	List<List<Point>> prev = new ArrayList<List<Point>>();

	
    	for(int i=0; i<100; i++)
    	{
	    
    		List row = new ArrayList<Point>();
    		for (int j=0; j<100; j++)
    		{
    			row.add(null);
    		}
    		prev.add(row);
    	}

	
	Point current = null;
        List<Point> explore = new ArrayList<Point>();
        explore.add(s);
	
        while (current == null || map.get(current.x).get(current.y) != null)
        {
        	if(explore.size() == 0) 
        	{
        		return null; //no path is known
        	}
        	else 
        	{
        		current = explore.get(0);
        		explore.remove(explore.get(0));   
        	}
	    
        	for(List<Integer> offset: offsets)
        	{
        		// if cell is in grid, not mud or water and parent is not set
        		Point adjacent = new Point(current.x+offset.get(0), current.y+offset.get(1));
        		if(adjacent.x >=0 && adjacent.x <= 100 && adjacent.y >=0 && adjacent.y <= 100) 
        		{
        			if(prev.get(adjacent.x).get(adjacent.y) == null)
        			{
				    if (map.get(adjacent.x).get(adjacent.y) == null || map.get(adjacent.x).get(adjacent.y).getC() != 2)
        				{
        					// set parent as current
        					// append cell to explore list (unless cell == end, then add to front of list)
        					prev.get(adjacent.x).set(adjacent.y, new Point( current.x, current.y ));
        					explore.add(adjacent);
        				}
        			}
        		}
        	}
        }
	
	
        List<Point> p = new ArrayList<Point>();
        while(current != s) 
        {
        	p.add(current);
        	current = prev.get(current.x).get(current.y);
        }
        p.add(s);
        Collections.reverse(p);	
     
	return p;
    }

    
    public MazeSolver(Point start, Point end, ArrayList<ArrayList<Record>> map)
    {
    	this.start = start;
    	this.end = end;
    	maze = map;
	
    	parents = new ArrayList<List<Point>>();

	
    	for(int i=0; i<100; i++)
    	{
	    
    		List row = new ArrayList<Point>();
    		for (int j=0; j<100; j++)
    		{
    			row.add(null);
    		}
    		parents.add(row);
    	}

    	for(int i=-1; i<2; i++)
    	{
    		for(int j=-1; j<2; j++)
    		{
    			if(i!=0 || j!=0)
    			{
    				List offset = new ArrayList<Integer>();
    				offset.add(i);
    				offset.add(j);
    				offsets.add(offset);
    			}
    		}
	    }
	}
	
    
    // Build Methods to traverse it
    public void solve()
    {
    	Point current = null;
        List<Point> explore = new ArrayList<Point>();
        explore.add(start);
	
        while (current != end)
        {
        	if(explore.size() == 0) 
        	{
        		return; //no path is known
        	}
        	else 
        	{
        		current = explore.get(0);
        		explore.remove(explore.get(0));   
        	}
	    
        	for(List<Integer> offset: offsets)
        	{
        		// if cell is in grid, not mud or water and parent is not set
        		Point adjacent = new Point(current.x+offset.get(0), current.y+offset.get(1));
        		if(adjacent.x >=0 && adjacent.x <= 100 && adjacent.y >=0 && adjacent.y <= 100) 
        		{
        			if(parents.get(adjacent.x).get(adjacent.y) == null && maze.get(adjacent.x).get(adjacent.y) != null)
        			{
        				if (maze.get(adjacent.x).get(adjacent.y).getC() == 0)
        				{
        					// set parent as current
        					// append cell to explore list (unless cell == end, then add to front of list)
        					parents.get(adjacent.x).set(adjacent.y, new Point( current.x, current.y ));
        					explore.add(adjacent);
        				}
        			}
        		}
        	}
        }
        path = new ArrayList<Point>();
        while(current != start) 
        {
        	path.add(current);
        	current = parents.get(current.x).get(current.y);
        }
        path.add(start);
        Collections.reverse(path);	
    }
}
