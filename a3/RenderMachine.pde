
/**
 * Responsible for rendering the current state of the simulation.
 */
class RenderMachine {
  private final ArrayList<Node> nodes;
  private final ArrayList<Spring> springs;
 
  public RenderMachine(ArrayList<Node> nodes, ArrayList<Spring> springs) {
    this.nodes = nodes;
    this.springs = springs;
  } 
  
  public void render() {
    renderSprings();
    renderNodes();
  }
  
  private void renderSprings() {
    for (Spring s : springs) {
      s.render();
    }
  }
  
  private void renderNodes() {
    for (Node n : nodes) {
      n.render();
    } 
  }
}
