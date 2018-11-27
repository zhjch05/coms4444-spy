package spy.g2;

import java.util.*;
import spy.sim.*;
import spy.g2.Pair;



public class Player implements spy.sim.Player {
    private class Pair implements Comparable<Pair>{
        public Point pt;
        public int dist;

        public Pair(Point p, int t){
            pt = p;
            dist = t;
        }

        @Override
        public int compareTo(Pair other) {
            return new Integer(dist).compareTo(other.dist);
        }
    }

    private ArrayList<ArrayList<Record>> records;
    private int id;
    private Point loc;
    private List<Point> water;
    
    private Boolean package_found;
    private Boolean target_found;

    private Point package_loc;
    private Point target_loc;
    
    private boolean[][] viewed;
    private int[][] graph;

    private List<Point> Friend;
    private HashMap<Integer, Integer> Met;
    private List<Point> todo;


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

        this.graph = new int[100][100];
        this.viewed = new boolean[100][100];
        for (int i=0; i<99; i++){
            for (int j=0; j<99; j++){
                viewed[i][j] = false;
            }
        }

        this.loc = startingPos;
        this.water = waterCells;

        for (Point p: waterCells){
            viewed[p.x][p.y] = true; 
            graph[p.x][p.y] = -2;
        }

        this.package_found = false;
        this.package_loc = null;

        this.target_found = false;
        this.target_loc = null;

        this.todo = new ArrayList<Point>();
        this.Met = new HashMap<Integer,Integer>();
        this.Friend = new ArrayList<Point>();
        for (int i=0; i<n; i++){
            Met.put(i,0);
        }
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
            
            viewed[p.x][p.y] = true;
            
            if (status.getC() == 0){
                graph[p.x][p.y] = 0;
            }
            
            else if (status.getC() == 1){
                graph[p.x][p.y] = -1;
            }
            
            if (status.getPT() == 1){
                this.package_found = true;
                this.package_loc = p;
                graph[p.x][p.y] = 1;
            }
            else if (status.getPT() == 2) {
                this.target_found = true;
                this.target_loc = p; 
                graph[p.x][p.y] = 1;
            }

            if (status.getPresentSoldiers().size() != 0){
                for (Integer i: status.getPresentSoldiers()){
                    if (i == this.id){
                        continue;
                    }
                    if (Met.get(i) < 15){
                        Friend.add(p);
                        break;
                    }
                }
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
        Met.put(id,15);
        return toSend;
    }

    public void receiveRecords(int id, List<Record> rec)
    {

        for (Record r : rec){
            Point p = r.getLoc();

            if (viewed[p.x][p.y]){
                continue;
            }
            records.get(r.getLoc().x).set(r.getLoc().y, r);
            viewed[p.x][p.y] = true;

            if (r.getC() == 0){
                graph[p.x][p.y] = 0;
            }
            
            if (r.getC() == 1){
                graph[p.x][p.y] = -1;
            }
            if (r.getPT() == 1){
                this.package_found = true;
                this.package_loc = r.getLoc();
                graph[p.x][p.y] = 1;
            }
            else if (r.getPT() == 2) {
                this.target_found = true;
                this.target_loc = r.getLoc();
                graph[p.x][p.y] = 2;
            }
        }
    }
    
    public List<Point> proposePath(){
        return find_path(package_loc, target_loc);
    }
    
    public List<Integer> getVotes(HashMap<Integer, List<Point>> paths){
        List<Integer> votes = new ArrayList<Integer>();
        for (Integer p: paths.keySet()){
            votes.add(p);
        }
        return votes;
    }
    
    public void receiveResults(HashMap<Integer, Integer> results)
    {
    }
    
    public Point getMove(){
        double max_reward = 0;
        Point best_move = new Point(-1000,-1000);
                
        System.out.printf("id: %d Current loc: %d %d \n", this.id, loc.x, loc.y);

        /*
        int ct = 0;
        for (int i=0; i<100; i++){
            for (int j=0; j<100; j++){
                if (viewed[i][j]==false){
                    ct++;
                }
            }
        }
        System.out.printf("remaining cells: %d \n", ct);
        for (Integer i: Met.keySet()){
            if (Met.get(i) > 0){
                Met.put(i, Met.get(i)-1);
            }
        }
        */
        if (todo.size() > 0){
            return todo.remove(todo.size()-1);
        }

        if (package_found && target_found){
            if (find_path(package_loc, target_loc) != null){
                
                System.out.printf("%d moving towards package\n",id);

                if (loc == package_loc){
                    return new Point(0,0);
                }
                else{
                    return move_toward(package_loc);
                }
            }
        }
        /*
        if (routeFound) {
            System.out.println("route found");
            if (!loc.equals(package_loc)) {
                // System.out.println("moving towards package location");
                return move_toward(package_loc);
            }
            else {
                // stay at package loc
                return new Point(0,0);
            }
        }
        
        if (Friend.size() != 0 && move_possible(Friend.get(0))){
            System.out.println("Moving towards friend");
            Point p = Friend.remove(0);
            System.out.printf("at %d %d \n", p.x, p.y);
            return move_toward(p, possible_move);
        }
        
        if (!readyForRoute && (loc.equals(package_loc) || loc.equals(target_loc))) {
            System.out.println("package or target found");
            readyForRoute = true;
            if (loc.equals(package_loc)) {
                start = package_loc;
                destination = target_loc;
            }
            else {
                start = target_loc;
                destination = package_loc;
            }
        }

        if (readyForRoute) {
            System.out.println("moving towards destination");
            System.out.println("loc x = " + this.loc.x + ", y = " + this.loc.y);
            Point next = findValidPathOneMove(loc);
            // System.out.println("x = " + next.x + ", y = " + next.y);
            // target found
            if (next == null) {
                return move_toward(package_loc);
            }

            // this.loc = next;
            //System.out.println("x = " + next.x + ", y = " + next.y);
            // System.out.println("x = " + this.loc.x + ", y = " + this.loc.y);
            return next;
        }

        if (package_loc != null && !loc.equals(package_loc) && move_possible(package_loc)){
            System.out.println("moving toward package");
            // return move_toward(package_loc);
            Point next = move_toward(package_loc);
            System.out.println("x = " + next.x + ", y = " + next.y);
            return next;
        }

        if (target_loc != null && !loc.equals(target_loc)) {
            return move_toward(target_loc);
        }
        */
        return explore();
    }

    // add all neighboring points to a list
    // return : a List contains all the neighbors of Point p, including itself
    public List<Point> neighbors(Point p){
        List<Point> neighbors = new ArrayList<Point>();
        for (int i=0; i<3; i++){
            for (int j=0; j<3; j++){
                int dx = p.x+i-1;
                int dy = p.y+j-1;
                if (i==1 && j==1){
                    continue;
                }
                if (dx < 0  || dy < 0 || dx > 99 || dy > 99 || graph[dx][dy] == -2){
                    continue;
                }
                neighbors.add(new Point(dx,dy));
            }
        }
        return neighbors;
    }
    
    public int exploration_reward(Point p){
        int reward = 0;
        if (graph[p.x][p.y] == 0){
            return reward;
        }
        for (int i=0; i<100; ++i){
            for (int j=0; j<100; ++j){
                if (Math.abs(i-p.x) < 3 && Math.abs(j-p.y) < 3){
                    if (viewed[i][j]){
                        continue;
                    }
                    reward ++;
                }
            }
        }
        return reward;
    }

    public Point explore(){
        Map<Point, Point> par = new HashMap<Point, Point>();
        int[][] visited = new int[100][100];
        PriorityQueue<Pair> pQueue = new PriorityQueue<Pair>(); 
        double max_reward = 0;
        boolean flag = true;
        Point dest = new Point(-100,-100);

        //+--------------------------------------------------------------------------+
        //| find the cell with max exploration / dist                                |
        //+--------------------------------------------------------------------------+
        pQueue.add(new Pair(loc,0));
        while(pQueue.size() > 0 && flag){
            Pair pa = pQueue.poll();
            Point p = pa.pt;
            int dist = pa.dist;
            System.out.printf("%d %d\n",p.x,p.y);

            visited[p.x][p.y] = 1;
            for (Point n: neighbors(p)){
                if (visited[n.x][n.y]==1 || graph[n.x][n.y] == -2){
                    continue;
                }
                System.out.printf("%d %d\n",n.x,n.y);

                if ((double)exploration_reward(n)/cost(p,n) > max_reward){
                    dest = n;
                    flag = false;
                    max_reward = (double)exploration_reward(n)/cost(p,n);
                }
                if (graph[n.x][n.y] > 0){
                    pQueue.add(new Pair(n, dist+cost(p,n)));
                    par.put(n,p);
                }
                visited[n.x][n.y] = 1;
            }
        }
        Point cur = dest;
        System.out.printf("dest: %d %d \n",cur.x, cur.y);
        while (par.containsKey(cur)){
            todo.add(new Point(cur.x,cur.y));
            cur = par.get(cur);
        }
        for (Point p:todo){
            System.out.printf("going: %d %d \n",p.x, p.y);
        }

        if (todo.size()>0){
            return todo.remove(todo.size()-1);
        }
        else{
            return new Point(0,0);
        }
    }

    public Point move_toward(Point dest){
        Map<Point, Point> par = new HashMap<Point, Point>();
        int[][] visited = new int[100][100];
        PriorityQueue<Pair> pQueue = new PriorityQueue<Pair>(); 
        pQueue.add(new Pair(loc,0));
        while(pQueue.size() > 0){
            Pair pa = pQueue.poll();
            Point p = pa.pt;
            int dist = pa.dist;
            visited[p.x][p.y] = 1;
            for (Point n: neighbors(p)){
                if (n.x== dest.x && n.y == dest.y){
                    par.put(n,p);
                    Point cur = n;

                    System.out.printf("%d dest: %d %d\n",id, dest.x,dest.y);

                    while (par.containsKey(cur)){
                        todo.add(new Point(cur.x - par.get(cur).x,cur.y - par.get(cur).y));
                        cur = par.get(cur);
                    }
                    if (todo.size()>0){
                        return todo.remove(todo.size()-1);
                    }
                    return new Point(0,0);
                }
                if (visited[n.x][n.y] == 0){
                    if (graph[n.x][n.y] > 0){
                        pQueue.add(new Pair(n, dist+cost(p,n)));
                        par.put(n,p);
                    }
                    visited[n.x][n.y] = 1;
                }
            }
        }
        return new Point(0,0);
    }

    public boolean move_possible(Point dest){
        List<Point> t = new ArrayList<Point>();
        int [][] visited = new int[100][100];
        t.add(loc);
        while(t.size() > 0){
            Point p = t.remove(t.size()-1);
            visited[p.x][p.y] = 1;
            for (Point n: neighbors(p)){
                if (n.x == dest.x && n.y==dest.y){
                    return true;
                }
                if (visited[n.x][n.y] == 0){
                    if (graph[n.x][n.y] > 0){
                        t.add(n);
                    }
                    visited[n.x][n.y] = 1;
                }
            }
        }
        return false;
    }

    public List<Point> find_path(Point start, Point dest){
        List<Point> path = new ArrayList<Point>();

        Map<Point, Point> par = new HashMap<Point, Point>();
        int[][] visited = new int[100][100];
        PriorityQueue<Pair> pQueue = new PriorityQueue<Pair>(); 

        pQueue.add(new Pair(start,0));
        Point cur = this.loc;
        boolean path_found = true;
        
        while(pQueue.size() > 0 && path_found){
            Pair pa = pQueue.poll();

            Point p = pa.pt;
            int dist = pa.dist;

            visited[p.x][p.y] = 1;

            for (Point n: neighbors(p)){
                if (graph[n.x][n.y] < 0 || visited[n.x][n.y] == 1){
                    continue;
                }
                if (n == dest){
                    cur = n;
                    path_found = false;
                }
                pQueue.add(new Pair(n, dist+cost(p,n)));
                par.put(n,p);
            }
        }
        while (par.containsKey(cur)){
            path.add(new Point(cur.x, cur.y));
            cur = par.get(cur);
        }
        return path;
    }

    public int cost(Point p, Point n){
        if (Math.abs(n.x-p.x)+Math.abs(n.y-p.y)==1){
            if (graph[n.x][n.y]==1){
                return 2;
            }
            else{
                return 4;
            }
        }
        else if (Math.abs(n.x-p.x)+Math.abs(n.y-p.y)==2){
            if (graph[n.x][n.y]==1){
                return 3;
            }
            else{
                return 6;
            }   
        }
        return 10;    
    }

    public int dist(Point a, Point b){
        int dx = Math.abs(a.x-b.x);
        int dy = Math.abs(a.y-b.y);
        int dst = 0;
        while (dx > 0 && dy > 0){
            dx = dx -1;
            dy = dy -1;
            dst += 3;
        }
        dst += 2*(dx+dy);
        return dst;
    }
}
