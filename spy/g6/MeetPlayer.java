package spy.g6;

import java.util.*;

import spy.sim.Point;
import spy.sim.Record;

public class MeetPlayer extends MovementTask {

    private int targetPlayerID;
    private int myPlayerID;
    private Point myPlayerLoc;
    private Point targetLoc;
    private Set<Point> waterSet;
    private boolean abort = true;
    private int roundCount = 0;

    private static final int EXPIRATION = 10;
    private static final boolean QUINCY_PRTCL = true;

    public MeetPlayer(int targetPlayerID, int myPlayerID, Point myPlayerLoc, List<Point> waterCells) {
        super();
        this.targetPlayerID = targetPlayerID;
        this.myPlayerID = myPlayerID;
        this.myPlayerLoc = myPlayerLoc;
        this.waterSet = new HashSet<>();
        waterSet.addAll(waterCells);
        this.moves = new LinkedList<>();
        targetLoc = findTargetLoc();
    }

    public Point findTargetLoc() {
        List<Point> myObs = withinObservationRadius(myPlayerLoc);
        if (myObs == null) return null;
        for (Point p : myObs) {
            if (targetLoc.equals(p)) return p;
        }
        return null;
    }

    private boolean inMap(Point p) {
        return p.x >= 0 && p.x < 100 && p.y >= 0 && p.y < 100;
    }

    private boolean inMap(int x, int y) {
        return x >= 0 && x < 100 && y >= 0 && y < 100;
    }

    private List<Point> withinObservationRadius(Point center) {
        ArrayList<Point> candidates = new ArrayList<>();
        if (inMap(center)) {
            candidates.add(new Point(center.x, center.y));
        }
        if (inMap(center.x - 1, center.y)) {
            candidates.add(new Point(center.x - 1, center.y));
        }
        if (inMap(center.x - 2, center.y)) {
            candidates.add(new Point(center.x - 2, center.y));
        }
        if (inMap(center.x - 1, center.y + 1)) {
            candidates.add(new Point(center.x - 1, center.y));
        }
        if (inMap(center.x - 1, center.y - 1)) {
            candidates.add(new Point(center.x - 1, center.y));
        }
        if (inMap(center.x, center.y + 1)) {
            candidates.add(new Point(center.x - 1, center.y));
        }
        if (inMap(center.x, center.y + 2)) {
            candidates.add(new Point(center.x - 1, center.y));
        }
        if (inMap(center.x, center.y - 1)) {
            candidates.add(new Point(center.x - 1, center.y));
        }
        if (inMap(center.x, center.y - 2)) {
            candidates.add(new Point(center.x - 1, center.y));
        }
        if (inMap(center.x + 1, center.y)) {
            candidates.add(new Point(center.x - 1, center.y));
        }
        if (inMap(center.x + 1, center.y + 1)) {
            candidates.add(new Point(center.x - 1, center.y));
        }
        if (inMap(center.x + 1, center.y - 1)) {
            candidates.add(new Point(center.x - 1, center.y));
        }
        if (inMap(center.x + 2, center.y)) {
            candidates.add(new Point(center.x - 1, center.y));
        }
        return candidates;
    }

    private List<Point> intersectionOfPoints(List<Point> A, List<Point> B) {
        ArrayList<Point> T = new ArrayList<>();
        Collections.copy(T, A);
        T.retainAll(B);
        return T;
    }

    private List<Point> filterToAvailablePoints(List<Point> cells) {
        List<Point> ret = new ArrayList<>();
        for(Point p: cells){
            if(!waterSet.contains(p)){
                ret.add(p);
            }
        }
        return ret;
    }

    private List<Point> getMoves(Point cur){
        //TODO
        return null;
    }

    //from myLoc to targetLoc
    private List<Point> findPath(List<Point> availableCells) {
        //bfs
        Queue<Point> q = new LinkedList<>();
        Map<Point, Point> parent = new HashMap<>();
        List<Point> ret = new ArrayList<>();
        q.add(myPlayerLoc);
        parent.put(myPlayerLoc, myPlayerLoc);
        while(!q.isEmpty()){
            Point cur = q.poll();
            if(cur.equals(targetLoc)){
                break;
            }
            for(Point p: getMoves(cur)){
                q.add(p);
                parent.put(p, cur);
            }
        }
        Point itr = targetLoc;
        Stack<Point> stk = new Stack<>();
        while(!itr.equals(myPlayerLoc)){
            stk.push(itr);
            itr = parent.get(itr);
        }
        stk.push(myPlayerLoc);
        while(!stk.isEmpty()){
            ret.add(stk.pop());
        }
        return ret;
    }

    @Override
    public boolean isCompleted() {
        // TODO

        return false;
    }

    @Override
    public Point nextMove() {
        if (moves.isEmpty()) {
            if (updateMoves()) {
                return super.nextMove();
            } else return null;
        } else return super.nextMove();
    }

    private boolean shouldMeet() {
        return targetLoc != null;
    }

    private boolean shouldWait() {
        if (!QUINCY_PRTCL) return false;
        if (roundCount > EXPIRATION) return false;
        if (myPlayerID < targetPlayerID) {
            moves.clear();
            moves.add(myPlayerLoc);
            roundCount++;
            return true;
        }
        return false;
    }

    private boolean updateMoves() {
        if (abort) return false;
        if (!shouldMeet()) return false;
        if (shouldWait()) return true;
        else {
            List<Point> myObs = withinObservationRadius(myPlayerLoc);
            if (myObs == null) return false;
            List<Point> targetObs = withinObservationRadius(targetLoc);
            List<Point> intersectionCells = intersectionOfPoints(myObs, targetObs);
            List<Point> availableCells = filterToAvailablePoints(intersectionCells);
            List<Point> path = findPath(availableCells);
            if (path == null) return false;
            moves.clear();
            moves.addAll(path);
        }
        return true;
    }
}
