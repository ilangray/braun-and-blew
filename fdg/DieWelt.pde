// Used to return an ArrayList of Springs and an ArrayList of Nodes to the Simulator
class DieWelt {
  public final ArrayList<Node> nodes;
  public final ArrayList<Spring> springs;
  public final ArrayList<Zap> zaps;
  public final ArrayList<Damper> dampers;
  public final Rect bounds;
  
  public DieWelt(ArrayList<Node> nodes, ArrayList<Spring> springs, ArrayList<Zap> zaps, 
    ArrayList<Damper> dampers, Rect bounds) {
    this.nodes = nodes;
    this.springs = springs;
    this.zaps = zaps;
    this.dampers = dampers;
    this.bounds = bounds;
  }
}