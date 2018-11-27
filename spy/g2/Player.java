package spy.g2;

import java.util.*;
import spy.sim.*;
import spy.g2.Pair;



public class Player implements spy.sim.Player {

    private ArrayList<ArrayList<Record>> records;
    private ArrayList<ArrayList<Record>> map;
    private ArrayList<ArrayList<Boolean>> visited;
    private int id;
    private Point loc;
    private List<Point> water;
    private boolean package_found;
    private Point package_loc;
    private Point target_loc;
    private List<Point> route;
    private boolean readyForRoute;
    private boolean routeFound;
    private Point start;
    private Point destination;
    
    private boolean[][] viewed;
    private int[][] graph;

    private List<Point> Friend;
    private HashMap<Integer, Integer> Met;
    private List<Point> todo;


    public void init(int n, int id, int t, Point startingPos, List<Point> waterCells, boolean isSpy)
    {
        this.id = id;
        this.records = new ArrayList<ArrayList<Record>>();
        // this.map = new ArrayList<ArrayList<Record>>();
        this.visited = new ArrayList<ArrayList<Boolean>>();
        this.route = new ArrayList<Point>();
        
        for (int i = 0; i < 100; i++)
        {
            ArrayList<Record> row = new ArrayList<Record>();
            // ArrayList<Record> r = new ArrayList<Record>();
            ArrayList<Boolean> v = new ArrayList<Boolean>();
            for (int j = 0; j < 100; j++)
            {
                row.add(null);
                // r.add(null);
                v.add(false);
            }
            this.records.add(row);
            // this.map.add(r);
            this.visited.add(v);
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
        }
        this.package_found = false;
        this.package_loc = null;

        this.target_loc = null;
        this.readyForRoute = false;
        this.routeFound = false;

        this.start = null;
        this.destination = null;
        this.todo = new ArrayList<Point>();
        this.Met = new HashMap<Integer,Integer>();
        for (int i=0; i<n; i++){
            Met.put(i,0);
        }
    }
    
    public void observe(Point loc, HashMap<Point, CellStatus> statuses)
    {
        this.loc = loc;
        this.Friend = new ArrayList<Point>();
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
                graph[p.x][p.y] = 1;
            }
            else if (status.getC() == 1){
                graph[p.x][p.y] = 2;
            }
            
            if (status.getPT() == 1){
                this.package_found = true;
                this.package_loc = p;
                if (start != null && this.target_loc != null && start.equals(this.target_loc)) {
                    destination = this.package_loc;
                }
            }
            else if (status.getPT() == 2) {
                // System.out.println("target found");
                this.target_loc = p;
                if (start != null && this.package_loc != null && start.equals(this.package_loc)) {
                    // System.out.println("destination assigned");
                    destination = this.target_loc;
                    // System.out.println("destination: x = " + destination.x + ", y = " + destination.y);
                }
            }

            if (status.getPresentSoldiers().size() != 0){
                for (Integer i: status.getPresentSoldiers()){
                    if (i== this.id){
                        continue;
                    }
                    if (Met.get(i) < 15){
                        Friend.add(p);
                        break;
                    }
                }
            }
            record.getObservations().add(new Observation(this.id, Simulator.getElapsedT()));
            // map.get(p.x).set(p.y, new Record(p, status.getC(), status.getPT(), new ArrayList<Observation>()));
        }
        this.map = records;
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
            if (records.get(r.getLoc().x).get(r.getLoc().y) == null){
                records.get(r.getLoc().x).set(r.getLoc().y, r);
                viewed[r.getLoc().x][r.getLoc().y] = true;
                if (r.getC() == 1){
                    graph[r.getLoc().x][r.getLoc().y] = 1;
                }
                if (r.getPT() == 1){
                    this.package_found = true;
                    this.package_loc = r.getLoc();
                    if (start != null && this.target_loc != null && start.equals(this.target_loc)) {
                        destination = this.package_loc;
                    }
                }
                else if (r.getPT() == 2) {
                    // System.out.println("target found");
                    this.target_loc = r.getLoc();
                    if (start != null && this.package_loc != null && start.equals(this.package_loc)) {
                        // System.out.println("destination assigned");
                        destination = this.target_loc;
                        // System.out.println("destination: x = " + destination.x + ", y = " + destination.y);
                    }
                }
            }
        }
    }
    
    public List<Point> proposePath(){
        System.out.println("Proposing a path"); 
        List<Point> path = new ArrayList<Point>();
        Map<Point, Point> par = new HashMap<Point, Point>();
        int[][] visited = new int[100][100];
        PriorityQueue<Pair> pQueue = new PriorityQueue<Pair>(10000, new PairComparator()); 
        pQueue.add(new Pair(loc,0));
        Point cur = this.loc;
        boolean flag = true;
        //+--------------------------------------------------------------------------+
        //| find the cell with max exploration / dist                                |
        //+--------------------------------------------------------------------------+
        
        while(pQueue.size() > 0 && flag){
            Pair pa = pQueue.poll();
            Point p = pa.pt;
            int dist = pa.dist;
            visited[p.x][p.y] = 1;
            for (Point n: neighbors(p)){
                if (graph[n.x][n.y] != 1){
                    continue;
                }
                if (n == target_loc){
                    cur = n;
                    flag = false;
                }
                if (visited[n.x][n.y] == 0){
                    pQueue.add(new Pair(n, dist+cost(p,n)));
                    par.put(n,p);
                    visited[n.x][n.y] = 1;
                }
            }
        }
        System.out.printf("found path\n");
        while (par.containsKey(cur)){
            path.add(new Point(cur.x - par.get(cur).x,cur.y - par.get(cur).y));
            cur = par.get(cur);
        }
        return path;
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
        Point best_move = new Point(0,0);
        Point loc = this.loc;
        
        /*
        System.out.printf("id: %d Current loc: %d %d \n", this.id, loc.x, loc.y);
        if (package_loc != null)
            System.out.printf("package loc: %d %d \n", package_loc.x, package_loc.y);
        */

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
        */        
        // find all possible movements
        for (Integer i: Met.keySet()){
            if (Met.get(i) > 0){
                Met.put(i, Met.get(i)-1);
            }
        }
        
        if (todo.size() > 0){
            return todo.remove(todo.size()-1);
        }
        else{
            System.out.printf("%d complete todo\n",id);
        }

        if (package_loc != null && target_loc != null){
            if (find_path(package_loc, target_loc)){
                System.out.printf("%d moving towards package\n",id);
                if (loc == package_loc){
                    return new Point(0,0);
                }
                todo = new ArrayList<Point>();
                return move_toward(package_loc);
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

    public Map<Point, Integer> check_possible_movement(Point lc){
        Map<Point,Integer> possible_move = new HashMap<Point, Integer>();
        for (int i=0; i<3; i++){
            for (int j=0; j<3; j++){
                int dx = lc.x+i-1;
                int dy = lc.y+j-1;

                if (i == 1 && j == 1){
                    continue;
                }
                if (dx < 0  || dy < 0 || dx > 99 || dy > 99 ){
                    continue;
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
        return possible_move;
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
                if (dx < 0  || dy < 0 || dx > 99 || dy > 99 ){
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
        for (int i=0; i<map.size();++i){
            for (int j=0; j<map.get(i).size();++j){
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
        PriorityQueue<Pair> pQueue = new PriorityQueue<Pair>(10000, new PairComparator()); 
        pQueue.add(new Pair(loc,0));
        double max_reward = 0;
        boolean flag = true;
        Point cur = this.loc;

        //+--------------------------------------------------------------------------+
        //| find the cell with max exploration / dist                                |
        //+--------------------------------------------------------------------------+
        
        while(pQueue.size() > 0 && flag){
            Pair pa = pQueue.poll();
            Point p = pa.pt;
            int dist = pa.dist;

            visited[p.x][p.y] = 1;
            for (Point n: neighbors(p)){
                if ((double)exploration_reward(n)/cost(p,n) > max_reward){
                    cur = n;
                    flag = false;
                    max_reward = (double)exploration_reward(n)/cost(p,n);
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
        System.out.printf("dest: %d %d \n",cur.x, cur.y);
        while (par.containsKey(cur)){
            todo.add(new Point(cur.x - par.get(cur).x,cur.y - par.get(cur).y));
            cur = par.get(cur);
        }

        // System.out.printf("Moving to %d %d \n",cur.x, cur.y);
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
        PriorityQueue<Pair> pQueue = new PriorityQueue<Pair>(10000, new PairComparator()); 
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

    public boolean find_path(Point pa, Point dest){
        List<Point> t = new ArrayList<Point>();
        int [][] visited = new int[100][100];
        t.add(pa);
        while(t.size() > 0){
            Point p = t.remove(t.size()-1);
            visited[p.x][p.y] = 1;
            for (Point n: neighbors(p)){
                if (graph[n.x][n.y]!=1){
                    continue;
                }
                if (n.x == dest.x && n.y==dest.y){
                    return true;
                }
                if (visited[n.x][n.y] == 0){
                    t.add(n);    
                    visited[n.x][n.y] = 1;
                }
            }
        }
        return false;
    }

    class PairComparator implements Comparator<Pair>{           
        // Overriding compare()method of Comparator  
                    // for descending order of dist 
        public int compare(Pair s1, Pair s2) { 
            if (s1.dist < s2.dist) 
                return 1; 
            else if (s1.dist > s2.dist) 
                return -1; 
            return 0; 
        } 
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
    // private void findValidPath(Point start) {
    //     Point curr = start;
    //     Point destination = null;
    //     if (curr == package_loc) {
    //         destination = target_loc;
    //     }
    //     else {
    //         destination = package_loc;
    //     }
    //     route.add(curr);
    //     visited.get(curr.x).set(curr.y, true);
    //     while (target_loc == null || curr != destination) {
    //         Point next = getNextPos(curr);
    //         if (next != null) {
    //             route.add(next);
    //             visited.get(next.x).set(next.y, true);
    //             curr = next;
    //         }
    //         else {
    //             route.remove(this.route.size() - 1);
    //             curr = route.get(this.route.size() - 1);
    //         }
    //     }
    // }

    private Point findValidPathOneMove(Point p) {
        // System.out.println("find valid path begins");
        // System.out.println("p: x = " + p.x + ", y = " + p.y);
        if (destination != null && p.equals(destination)) {
            routeFound = true;
            // System.out.println("destination is not null or current point equals to destination");
            return null;
        }

        route.add(p);
        visited.get(p.x).set(p.y, true);
        Point next = getNextPos(p);
        if (next != null) {
            return new Point(next.x - p.x, next.y - p.y);
            // return next;
        }

        route.remove(route.size() - 1);
        // return route.remove(route.size() - 1);
        Point prev = route.remove(route.size() - 1);
        return new Point(prev.x - p.x, prev.y - p.y);
    }

    private Point getNextPos(Point curr) {
        // if not visited, not muddy, then go, otherwise return null
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                Point next = new Point(curr.x + i, curr.y + j);
                if (next.x < 0 || next.y < 0 || next.x > 99 || next.y > 99) {
                    continue;
                }
                if (visited.get(next.x).get(next.y)) {
                    continue;
                }

                if (records.get(next.x).get(next.y).getC() == 1) {
                    continue;
                }

                if (water.contains(next)) {
                    continue;
                }

                return next;
            }
        }

        return null;
    }
}
