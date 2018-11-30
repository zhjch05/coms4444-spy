package spy.g6;

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
    private ArrayList<ArrayList<Boolean>> knownCells;
    private ArrayList<ArrayList<Boolean>> waterCells;
    private int id;
    private Point loc;
    private Point nextMoveTarget;

    private Point[] nineMoves;

    public void init(int n, int id, int t, Point startingPos, List<Point> waterCells, boolean isSpy)
    {
        this.id = id;
        this.loc = startingPos;
        this.records = new ArrayList<>();
        this.knownCells = new ArrayList<>();
        this.waterCells = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ArrayList<Record> row = new ArrayList<>();
            ArrayList<Boolean> knownRow = new ArrayList<>();
            ArrayList<Boolean> waterRow = new ArrayList<>();
            for (int j = 0; j < 100; j++) {
                row.add(null);
                knownRow.add(Boolean.FALSE);
                waterRow.add(Boolean.FALSE);
            }
            this.records.add(row);
            this.knownCells.add(knownRow);
            this.waterCells.add(waterRow);
        }
        for(Point p: waterCells){
            knownCells.get(p.x).set(p.y, Boolean.TRUE);
            this.waterCells.get(p.x).set(p.y, Boolean.TRUE);
        }

        nineMoves = new Point[9];
        nineMoves[0] = new Point(-1, -1);
        nineMoves[1] = new Point(-1, 0);
        nineMoves[2] = new Point(-1, 1);
        nineMoves[3] = new Point(0, 1);
        nineMoves[4] = new Point(0, 0);
        nineMoves[5] = new Point(0, 1);
        nineMoves[6] = new Point(1, -1);
        nineMoves[7] = new Point(1, 0);
        nineMoves[8] = new Point(1, -1);
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
            record.getObservations().add(new Observation(this.id, Simulator.getElapsedT()));
            knownCells.get(p.x).set(p.y, Boolean.TRUE);
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

    }

    public Point getMove()
    {
//        Random rand = new Random();
//        int x = rand.nextInt(2) * 2 - 1;
//        int y = rand.nextInt(2 + Math.abs(x)) * (2 - Math.abs(x)) - 1;
//        return new Point(x, y);
        if(nextMoveTarget == null){
            nextMoveTarget = sampleUnknown();
        }
        Point nextStep = stepForward(this.loc, nextMoveTarget);
        return nextStep;
    }

    private ArrayList<Point> getAvailableNextLocs() {
        ArrayList<Point> ret = new ArrayList<>();
        for(Point move: nineMoves) {
            Point nextLoc = new Point(loc.x + move.x, loc.y + move.y);
            if(!waterCells.get(nextLoc.x).get(nextLoc.y)){
                ret.add(nextLoc);
            }
        }
        return ret;
    }

    private Point sampleUnknown(){
        List<Point> candidates = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                if(!knownCells.get(i).get(j)){
                    candidates.add(new Point(i, j));
                }
            }
        }
        Random random = new Random();
        return candidates.get(random.nextInt(candidates.size()));
    }

    private Point stepForward(Point current, Point target){
        ArrayList<Point> nextLocs = getAvailableNextLocs();

        return new Point(0, 0);
    }
}
