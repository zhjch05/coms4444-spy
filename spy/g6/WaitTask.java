package spy.g6;

import java.util.LinkedList;

import spy.sim.Point;

public class WaitTask extends MovementTask {
	// protected LinkedList<Point> moves;
	

	//wait at point and avoid communication
	public WaitTask(PathFinder pathFinder, Point loc, Point packageLoc) {
		super();
		LinkedList<Point> points = pathFinder.startSearch(loc, packageLoc, false);
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
		// return false;
	}
	
	@Override
	public Point nextMove(){
		if (moves.isEmpty())
			return new Point(0, 0);
	 	return moves.removeFirst();
	}
}
