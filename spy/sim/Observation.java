package spy.sim;

public class Observation {

    private int id;
    private int t;
    
    public Observation(int id, int t)
    {
        this.id = id;
        this.t = t;
    }
    
    public int getID()
    {
        return this.id;
    }
    
    public int getT()
    {
        return this.t;
    }
    
    public String toString()
    {
        return "(id " + this.id + ", t " + this.t + ")";
    }
}
