package spy.sim;

import java.util.ArrayList;
import spy.sim.Point;
import spy.sim.Observation;

public class Record {
    Point loc;
    private int c;
    private int pt;
    private ArrayList<Observation> observations;
    
    public Record(Point loc, int c, int pt, ArrayList<Observation> observations)
    {
        this.loc = new Point(loc);
        this.c = c;
        this.pt = pt;
        this.observations = new ArrayList<Observation>(observations);
    }
    
    public Record(Record other)
    {
        this.loc = new Point(other.loc);
        this.c = other.c;
        this.pt = other.pt;
        this.observations = new ArrayList<Observation>(other.observations);
    }
    
    public Point getLoc()
    {
        return this.loc;
    }
    
    public int getC() {
        return this.c;
    }
    
    public int getPT() {
        return this.pt;
    }
    
    public ArrayList<Observation> getObservations() {
        return this.observations;
    }
    
    public String toString()
    {
        return "(Record for " + this.loc + ": " + (this.c == 1 ? "muddy" : "clear") + ", " + (this.pt == 0 ? "none" : (this.pt == 1 ? "package" : "target")) + ", " + observations + ")\n";
    }
}
