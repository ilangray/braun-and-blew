
/**
 * Responsible for rendering the current state of the simulation.
 */
class RenderMachine {
  
  private final int NODE_COLOR = color(0,0,0);
  private final int SPRING_COLOR = color(255,0,0);
  
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
      renderSpring(s);
    }
  }
  
  private void renderSpring(Spring s) {
    Point endA = s.endA.pos;
    Point endB = s.endB.pos;
   
    stroke(SPRING_COLOR);
    fill(SPRING_COLOR);
    line(endA.x, endA.y, endB.x, endB.y); 
  }
  
  private void renderNodes() {
    for (Node n : nodes) {
      renderNode(n);
    } 
  }
  
  private void renderNode(Node n) {
    Point center = n.pos;
    float radius = n.radius;
    
    stroke(NODE_COLOR);
    fill(NODE_COLOR);
    ellipse(center.x, center.y, radius, radius);
  }
}
