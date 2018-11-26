package spy.g4;

import java.util.List;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.HashSet;

import spy.sim.Point;
import spy.sim.Record;
import spy.sim.CellStatus;
import spy.sim.Simulator;
import spy.sim.Observation;

public class Player implements spy.sim.Player {
    
    private ArrayList<ArrayList<Record>> records;
    private int id;
    private Point loc;

    private boolean isSpy;

    private int spy = -1; // player who we think is the spy
    private HashMap<Integer, HashSet<Point>> possibleSpies; // each players mapped to suspicion count
    //private HashMap<Integer, Integer> suspicionScore;
    private HashMap<Point, Record> trueRecords;

    private HashMap<Point, CellStatus> previousStatuses;
    private boolean packageKnown = false;
    private boolean targetKnown = false;
    private boolean pathKnown = false;
    
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
            this.records.add(row);
        }

        this.isSpy = isSpy;
        trueRecords = new HashMap<Point, Record>();
        possibleSpies = new HashMap<Integer, HashSet<Point>>();
        //suspicionScore = new HashMap<Integer, Integer>();
        previousStatuses = new HashMap<Point, CellStatus>();
    }
    
    public void observe(Point loc, HashMap<Point, CellStatus> statuses)
    {

        previousStatuses = statuses;

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
                trueRecords.put(p, record);
                //System.out.println("observed a record at " + p);
            }
            record.getObservations().add(new Observation(this.id, Simulator.getElapsedT()));
        }
        /*for (int i=0; i<records.size(); i++) {
            int j=0;
            for (Record r : records.get(i)) {
                System.out.println(j + ": " + r);
                j++;
            }
        }*/
    }
    
    public List<Record> sendRecords(int id)
    {
        ArrayList<Record> toSend = new ArrayList<Record>();
        /*if ( (possibleSpies.size() > 0) && ((possibleSpies.get(id).size() > 30) || (id == spy))) {
            return toSend;
        } else {
            if (!isSpy) {
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
            } else {
                int m = 0;
                for (ArrayList<Record> row : records) {
                    for (Record record : row) {
                        if (record != null) {
                            if (m%3 == 0) {
                                //System.out.println("WE WANT TO LIE");
                                Record newRec = new Record(record.getLoc(), (record.getC()+1)%2, (record.getPT()+1)%3, record.getObservations());
                                toSend.add(newRec);
                            } else {
                                toSend.add(record);
                            }
                        }
                        m += 1;
                    }
                }
            }
            return toSend;
        }*/
        for (ArrayList<Record> recarray : records) {
            for (Record ourRecord : recarray) {
                if (ourRecord != null) {
                    ArrayList<Observation> observations = ourRecord.getObservations();
                    if ((observations.size() > 1) && (observations.get(observations.size() - 1).getID() != this.id)) {
                        observations.add(new Observation(this.id, Simulator.getElapsedT()));
                    }
                    toSend.add(ourRecord);
                }
            }
        }
        return toSend;
    }
    
    public void receiveRecords(int id, List<Record> records)
    {
        /*if (id != spy) {
            // Compare received records against those that we have in our trueRecords list
            // If there is a conflict
            for (Record recR : records) {
                Point p = recR.getLoc();
                Record ourRecord = this.trueRecords.get(p);
                if (ourRecord != null) {
                    if (!((ourRecord.getC() == recR.getC()) && (ourRecord.getPT() == recR.getPT()))) {
                        //System.out.println("player " + id + " LIED!");
                        if (!(possibleSpies.containsKey(id))) {
                            HashSet<Point> clashPs = new HashSet<Point>();
                            clashPs.add(p);
                            possibleSpies.put(id, clashPs);
                        } else {
                            if (!(possibleSpies.get(id).contains(p))) {
                                possibleSpies.get(id).add(p);
                            }
                        }
                        //System.out.println("p" + id + " suspicion score = " + possibleSpies.get(id).size());
                        if (recR.getObservations().size() < 2) {
                            spy = id;
                        }
                        
                    } else {
                        //System.out.println("player " + id + " told the TRUTH!");
                    }
                }
            }
        }*/

        // Assuming no spies
        for (Record recR : records) {

            if (recR != null) {
                Point p = recR.getLoc();
                Record ourRecord = this.records.get(p.x).get(p.y);

                if (ourRecord == null) {
                    ourRecord = new Record(p, recR.getC(), recR.getPT(), recR.getObservations());
                    this.records.get(p.x).set(p.y, ourRecord);
                }
            }
        }
        

        // If we are in the chain of observations
        // If we are first in the chain: compare information against our own obswerved records
        // If not first in chain: compare sequence of observations with the corresponding sequence of observations in records
        
    }

    public List<Point> calculatePath() {
        
        // ##########################
        // ### Quincy's code here ###
        // ##########################

        List<Point> finalPath = new ArrayList<Point>();

        // if there is a complete path, set pathKnown to true

        if (!targetKnown || !packageKnown) {
            pathKnown = false;
            return null;
        }
        //private HashMap<Point, Record> trueRecords;
        // find the package and target position first
        Point target = null;
        Point package = null;
        for (Point key : trueRecords.keySet()) {
            pt = trueRecords.get(key).getPT();
            if (pt == 1) { /* package location */
                package = key;
            }
            if (pt == 2) { /* target location */
                target = key;
            }
            if (target != null && package != null) {
                break;
            }
        }

        /* perform BFS */
        /* visited contains the point and the path that took to get to that point */
        HashMap<Point, List<Point>> visited = new HashMap<Point, List<Point>>();
        /* keeps track of parent / children pairs */
        List<List<Point>> queue = new ArrayList<ArrayList<Point>>();
        Boolean goal_reached = false;

        queue.add(new ArrayList<Point>(package, null));

        while (true) {
            /* dequeue and set to current */
            if (queue.size() == 0 && goal_reached == false) {
                break;
            }
            List<Point> temp = queue.get(0);
            queue.remove(temp);
            Point current = temp[0];
            Point parent = temp[1];

            /* goal test */
            if (current.equals(target)) {
                goal_reached = true;
            }
            /* add to visited */
            List<Point> path = new ArrayList<Point>();
            if (parent == null) {
                path.add(current);
            } else {
                path = visited.get(parent);
                path.add(current);
            }
            visited.add(current, path);


            /* if goal test successful */
            if (goal_reached = true) {
                break;
            }
            /* adds all children that's not visited to queue
             * only adds children that are normal condition */
            int x = current.x;
            int y = current.y;
            List<Point> children = new ArrayList<Point>();
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (i == 0 && j == 0) {
                        continue;
                    }
                    Point child = new Point(x + i, y + j);
                    /* check condition */
                    if (trueRecords.get(child).getC() != 0) {
                        continue;
                    }
                    /* check visited */
                    Boolean visited_child == false;
                    for (Point p : visited.keySet()) {
                        if (p.equals(child)) {
                            visited_child == true;
                        }
                    }
                    if (visited_child = true) {
                        continue;
                    }

                    children.add(new ArrayList<Point>(child, current));
                }
            }
        }

        if (goal_reached == false) {
            finalPath = null;
        } else {
            pathKnown = true;
            finalPath = visited.get(target);
        }


        return finalPath;
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
            return new ArrayList<Integer>(entry.getKey());
        }
        return null;
    }
    
    public void receiveResults(HashMap<Integer, Integer> results)
    {
        
    }
    
    public Point getMove()
    {
        if (pathKnown) {

            // ### MOVE TO PACKAGE ###

            // ##########################
            // ### Ashley's code here ###
            // ##########################

            return new Point(0, 0); // change this

            // probably will need to calculate the shortest path from current location to target first--store as a list of points
            // then each time getMove is called, iterate through the list of points and remove each one you visit until soldier has reached last point in list which should be the package location

        } else if (packageKnown || targetKnown) {

            // ### KEEP EXPLORING (currently might time out) ###

            // ##########################
            // ### Shandu's code here ###
            // ##########################

            //System.out.println("cellStatus from previous observation:");
            for (Point p : previousStatuses.keySet()) {
                CellStatus cs = previousStatuses.get(p);
                //System.out.println(p + ": " + cs.getC() + ", " + cs.getPT() + ", " + cs.getPresentSoldiers());
            }

            Random rand = new Random();
            int x = rand.nextInt(2) * 2 - 1;
            int y = rand.nextInt(2 + Math.abs(x)) * (2 - Math.abs(x)) - 1;
            return new Point(x, y);

        } else {

            // ### KEEP EXPLORING ###

            // ##########################
            // ### Shandu's code here ###
            // ##########################

            //System.out.println("cellStatus from previous observation:");
            for (Point p : previousStatuses.keySet()) {
                CellStatus cs = previousStatuses.get(p);
                //System.out.println(p + ": " + cs.getC() + ", " + cs.getPT() + ", " + cs.getPresentSoldiers());
            }

            Random rand = new Random();
            int x = rand.nextInt(2) * 2 - 1;
            int y = rand.nextInt(2 + Math.abs(x)) * (2 - Math.abs(x)) - 1;
            return new Point(x, y);

        }
        // ***NOTE*** currently both exploration blocks will do the same thing (the else-if and else statements)
    }
}
