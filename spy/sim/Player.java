package spy.sim;

import java.util.HashMap;
import java.util.List;

import spy.sim.Point;
import spy.sim.CellStatus;
import spy.sim.Record;

public interface Player {
    // Initialization function.
    // n: Total number of players (including this player).
    // id: ID of this player.
    // t: The time threshold for the game.
    // startingPos: The starting position of this player
    // waterCells: coordinates of all water cells.
    // isSpy: True if player is the spy. False otherwise.
    public void init(int n, int id, int t, Point startingPos, List<Point> waterCells, boolean isSpy);

    // Gives a HashMap of the observable cells along with the status of each sell.
    public void observe(HashMap<Point, CellStatus> statuses);
    
    // Gets the records to send to player with a particular ID
    public List<Record> sendRecords(int id);
    
    // Gives player the records sent from the player with a particular ID
    public void receiveRecords(int id, List<Record> records);
    
    // Gets a proposed path from a player at the package
    public List<Point> proposePath();
    
    // Gives a map from player ID to a path that player proposed
    public int getVote(HashMap<Integer, List<Point>> paths);
    
    // Recieves the results (in the event that no path succeeds).
    public void receiveResults(HashMap<Integer, Integer> results);
    
    // Gets the player's move, if any. A null return is treated like a move of 0,0
    public Point getMove();
}
