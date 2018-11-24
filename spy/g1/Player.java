package spy.g1;

import java.util.List;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;

import spy.sim.Point;
import spy.sim.Record;
import spy.sim.CellStatus;
import spy.sim.Simulator;
import spy.sim.Observation;

public class Player implements spy.sim.Player {

    private ArrayList<ArrayList<Record>> records;
    private int id;
    private Point loc;
    private HashSet water = new HashSet();
    private HashSet existingEdges = new HashSet();
    private Dijkstra djk = new Dijkstra();

    public void init(int n, int id, int t, Point startingPos, List<Point> waterCells, boolean isSpy)
    {
        // Hashmap of water cells for more efficient check
        for (Point w : waterCells){
          int x = w.x;
          int y = w.y;
          int[] p = {x,y};
          water.add(p);
        }

        // Construct Dijkstra graph of land cells
        this.id = id;
        this.records = new ArrayList<ArrayList<Record>>();
        for (int i = 0; i < 100; i++)
        {
            ArrayList<Record> row = new ArrayList<Record>();
            for (int j = 0; j < 100; j++)
            {
                int[] coords = {i,j};
                if(!water.contains(coords)){
                  String name = Integer.toString(i) + "," + Integer.toString(j);
                  Vertex newVertex = new Vertex(name,i,j);
                  djk.addVertex(newVertex);
                }

                row.add(null);
            }
            this.records.add(row);
        }
        for (Vertex source : djk.getVertices()){

          //Construct edge weights as if each land cell is muddy
          int i = source.x;
          int j = source.y;
          int[] northwest = {i-1, j+1};
          if (i > 0 && j < 99){
            if(!water.contains(northwest)){
              String name = Integer.toString(i) + "," + Integer.toString(j);
              Vertex target = djk.getVertex(name);
              Vertex[] key1 = {source, target};
              Vertex[] key2 = {target, source};
              if(! (existingEdges.contains(key1) || existingEdges.contains(key2)) ){
                djk.addUndirectedEdge(source.name, target.name, 6);
                existingEdges.add(key1);
                existingEdges.add(key2);
              }
            }
          }
          int[] west = {i-1, j};
          if (i > 0){
            if(!water.contains(west)){
              String name = Integer.toString(i) + "," + Integer.toString(j);
              Vertex target = djk.getVertex(name);
              Vertex[] key1 = {source, target};
              Vertex[] key2 = {target, source};
              if(! (existingEdges.contains(key1) || existingEdges.contains(key2)) ){
                djk.addUndirectedEdge(source.name, target.name, 4);
                existingEdges.add(key1);
                existingEdges.add(key2);
              }
            }
          }
          int[] southwest = {i-1, j-1};
          if (i > 0 && j > 0){
            if(!water.contains(southwest)){
              String name = Integer.toString(i) + "," + Integer.toString(j);
              Vertex target = djk.getVertex(name);
              Vertex[] key1 = {source, target};
              Vertex[] key2 = {target, source};
              if(! (existingEdges.contains(key1) || existingEdges.contains(key2)) ){
                djk.addUndirectedEdge(source.name, target.name, 6);
                existingEdges.add(key1);
                existingEdges.add(key2);
              }
            }
          }
          int[] north = {i, j+1};
          if (j < 99){
            if(!water.contains(north)){
              String name = Integer.toString(i) + "," + Integer.toString(j);
              Vertex target = djk.getVertex(name);
              Vertex[] key1 = {source, target};
              Vertex[] key2 = {target, source};
              if(! (existingEdges.contains(key1) || existingEdges.contains(key2)) ){
                djk.addUndirectedEdge(source.name, target.name, 4);
                existingEdges.add(key1);
                existingEdges.add(key2);
              }
            }
          }
          int[] south = {i, j-1};
          if (j > 0){
            if(!water.contains(south)){
              String name = Integer.toString(i) + "," + Integer.toString(j);
              Vertex target = djk.getVertex(name);
              Vertex[] key1 = {source, target};
              Vertex[] key2 = {target, source};
              if(! (existingEdges.contains(key1) || existingEdges.contains(key2)) ){
                djk.addUndirectedEdge(source.name, target.name, 4);
                existingEdges.add(key1);
                existingEdges.add(key2);
              }
            }
          }
          int[] northeast = {i+1, j+1};
          if (i < 99 && j < 99){
            if(!water.contains(northeast)){
              String name = Integer.toString(i) + "," + Integer.toString(j);
              Vertex target = djk.getVertex(name);
              Vertex[] key1 = {source, target};
              Vertex[] key2 = {target, source};
              if(! (existingEdges.contains(key1) || existingEdges.contains(key2)) ){
                djk.addUndirectedEdge(source.name, target.name, 6);
                existingEdges.add(key1);
                existingEdges.add(key2);
              }
            }
          }
          int[] east = {i+1, j};
          if (i < 99){
            if(!water.contains(east)){
              String name = Integer.toString(i) + "," + Integer.toString(j);
              Vertex target = djk.getVertex(name);
              Vertex[] key1 = {source, target};
              Vertex[] key2 = {target, source};
              if(! (existingEdges.contains(key1) || existingEdges.contains(key2)) ){
                djk.addUndirectedEdge(source.name, target.name, 4);
                existingEdges.add(key1);
                existingEdges.add(key2);
              }
            }
          }
          int[] southeast = {i+1, j-1};
          if (i < 99 && j < 99){
            if(!water.contains(southeast)){
              String name = Integer.toString(i) + "," + Integer.toString(j);
              Vertex target = djk.getVertex(name);
              Vertex[] key1 = {source, target};
              Vertex[] key2 = {target, source};
              if(! (existingEdges.contains(key1) || existingEdges.contains(key2)) ){
                djk.addUndirectedEdge(source.name, target.name, 6);
                existingEdges.add(key1);
                existingEdges.add(key2);
              }
            }
          }
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
        Random rand = new Random();
        int x = rand.nextInt(2) * 2 - 1;
        int y = rand.nextInt(2 + Math.abs(x)) * (2 - Math.abs(x)) - 1;
        return new Point(x, y);
    }
}
