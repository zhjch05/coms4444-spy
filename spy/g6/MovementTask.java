package spy.g6;

import java.util.LinkedList;

import spy.sim.Point;

public class MovementTask {
	protected LinkedList<Point> moves;
	
	public MovementTask() {
		
	}
	
	public MovementTask(LinkedList<Point> points) {
		moves = new LinkedList<Point>();
		Point prev = null;
		for (Point p: points) {
			if (prev != null) {
				//System.err.print(new Point(p.x - prev.x, p.y - prev.y));
				moves.addLast(new Point(p.x - prev.x, p.y - prev.y));
			}
			prev = p;
		}
	}
	
	public boolean isCompleted() {
		return moves.isEmpty();
	}
	
	public Point nextMove(){
		return moves.removeFirst();
	}
}

