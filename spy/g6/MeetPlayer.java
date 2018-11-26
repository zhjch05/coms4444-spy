package spy.g6;

import java.util.ArrayList;
import java.util.LinkedList;

import spy.sim.Point;
import spy.sim.Record;

public class MeetPlayer extends MovementTask {

	private int player;
	private ArrayList<ArrayList<ArrayList<Record>>> playerRecords;

	private ArrayList<ArrayList<Record>> getLatestRecord(){
	    if(playerRecords.isEmpty()) return null;
	    return playerRecords.get(0);
    }

	public MeetPlayer(int player, ArrayList<ArrayList<ArrayList<Record>>> playerRecords) {
		this.player = player;
		this.playerRecords = playerRecords;
	}



}
