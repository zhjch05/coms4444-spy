package spy.g2;

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

    private ArrayList<ArrayList<Record>> records;
    private ArrayList<ArrayList<Record>> map;
    private int id;
    private Point loc;
    private List<Point> water;
    private boolean package_found;
    private Point package_loc;


    // use a integer to keep track of state of the board, 0 represent empty, 1 represent muddy, 2 represent water
    // 3 represent package and 4 represent target
    public void init(int n, int id, int t, Point startingPos, List<Point> waterCells, boolean isSpy)
    {
        this.id = id;
        this.records = new ArrayList<ArrayList<Record>>();
        this.map = new ArrayList<ArrayList<Record>>();
        
        for (int i = 0; i < 100; i++)
        {
            ArrayList<Record> row = new ArrayList<Record>();
            ArrayList<Record> r = new ArrayList<Record>();
            for (int j = 0; j < 100; j++)
            {
                row.add(null);
                r.add(null);
            }
            this.records.add(row);
            this.map.add(r);
        }
        this.loc = startingPos;
        this.water = waterCells;
        this.package_found = false;
        this.package_loc = null;
    }
    
    public void observe(Point loc, HashMap<Point, CellStatus> statuses)
    {
        this.loc = loc;
        for (Map.Entry<Point, CellStatus> entry : statuses.entrySet())
        {
            Point p = entry.getKey();
            CellStatus status = entry.getValue();
            
            Record record = records.get(p.x).get(p.y);
            if (record == null || record.getC() != status.getC() || record.getPT() != status.getPT())
            {
                ArrayList<Observation> observations = new ArrayList<Observation>();
                record = new Record(p, status.getC(), status.getPT(), observations);
                records.get(p.x).set(p.y, record);
            }
            if (status.getPT() == 1){
                this.package_found = true;
                this.package_loc = p;
            }
            record.getObservations().add(new Observation(this.id, Simulator.getElapsedT()));
            map.get(p.x).set(p.y, new Record(p, status.getC(), status.getPT(), new ArrayList<Observation>()));
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
    
    public void receiveRecords(int id, List<Record> rec)
    {
    }
    
    public List<Point> proposePath()
    {
        return null;
    }
    
    public List<Integer> getVotes(HashMap<Integer, List<Point>> paths)
    {
        List<Integer> votes = new ArrayList<Integer>();
        for (Integer p: paths.keySet()){
            votes.add(p);
        }
        return votes;
    }
    
    public void receiveResults(HashMap<Integer, Integer> results)
    {
    }
    
    public Point getMove()
    {
        double max_reward = 0;
        Point best_move = this.loc;
        Map<Point, Integer> possible_move = new HashMap<Point, Integer>();
        Point loc = this.loc;

        if (package_found){
            return move_toward(package_loc);
        }
        for (int i=0; i<3; i++){
            for (int j=0; j<3; j++){
                int dx = loc.x+i-1;
                int dy = loc.y+j-1;
                if (i==1 && j==1){
                    continue;
                }
                if (map.get(dx).get(dy).getC() == 1){
                    possible_move.put(new Point(i-1,j-1),4);
                }
                if(this.water.contains(new Point(dx,dy))){
                    continue;
                }
                else{
                    if (Math.abs(i)+Math.abs(j)>1){
                        if (map.get(dx).get(dy).getC() == 1){
                            possible_move.put(new Point(i-1,j-1),6);
                        }
                        else{
                            possible_move.put(new Point(i-1,j-1),3);
                        }
                    }
                    else{

                        if (map.get(dx).get(dy).getC() == 1){
                            possible_move.put(new Point(i-1,j-1),4);
                        }
                        else{
                            possible_move.put(new Point(i-1,j-1),2);
                        }
                    }
                }
            }
        }
        for (Point p: possible_move.keySet()){
            Point nex = new Point(loc.x+p.x,loc.y+p.y);
            if (exploration_reward(nex)/possible_move.get(p)>max_reward){
                max_reward = exploration_reward(nex)/possible_move.get(p);
                best_move = p;
            }
            if (max_reward == 0){
                best_move = find_unknown(loc);
            }
        }
        return best_move;
    }

    public int exploration_reward(Point p){
        int reward = 0;
        for (int i=0; i<map.size();++i){
            for (int j=0; j<map.get(i).size();++j){
                if (Math.abs(i-p.x) < 3 && Math.abs(j-p.y) < 3){
                    if (map.get(i).get(j)==null){
                        reward ++;
                    }
                }
            }
        }
        return reward;
    }

    public Point find_unknown(Point loc){
        int minimum = 200;
        int tx = 0;
        int ty = 0;
        for (int i=0; i<map.size();++i){
            for (int j=0; j<map.get(i).size();++j){
                if (map.get(i).get(j)==null){
                    if (Math.abs(i-loc.x)+Math.abs(j-loc.y)< minimum){
                        minimum = Math.abs(i-loc.x)+Math.abs(j-loc.y);
                        tx = i;
                        ty = j;
                    }
                }
            }
        }
        return move_toward(new Point(tx, ty));
    }


    public Point move_toward(Point p){
        int dx = 0;
        int dy = 0;
        if (p.x > this.loc.x){
            dx = 1;
        }
        else if (p.x == this.loc.x){
            dx = 0;
        }
        else{
            dx = -1;
        }
        if (p.y > this.loc.y){
            dy = 1;
        }
        else if (p.y == this.loc.y){
            dy = 0;
        }
        else{
            dy = -1;
        }
        return new Point (dx, dy);
    }
}
