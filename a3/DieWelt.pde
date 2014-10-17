// Used to return an ArrayList of Springs and an ArrayList of Nodes to the Simulator
class DieWelt {
  public final ArrayList<Node> nodes;
  public final ArrayList<Spring> springs;
  public final ArrayList<Zap> zaps;
  
  public DieWelt(ArrayList<Nodes> nodes, ArrayList<Spring> springs) {
    this.nodes = nodes;
    this.springs = springs;
    this.zaps = zaps;
  }
}
