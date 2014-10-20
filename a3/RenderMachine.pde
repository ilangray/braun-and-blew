
/**
 * Responsible for rendering the current state of the simulation.
 */
class RenderMachine {
  
  private final int EMPTY_NODE_COLOR = color(0,0,0);
  private final int MOUSED_NODE_COLOR = color(0, 255, 0);
  
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
   
    if (dist(endA.x, endA.y, endB.x, endB.y) < s.restLen) {
      stroke(SPRING_COLOR);
      fill(SPRING_COLOR);
    } else {
      stroke(color(0, 0, 255));
      fill(color(0, 0, 255));
    }
    
    line(endA.x, endA.y, endB.x, endB.y); 
  }
  
  private void renderNodes() {
    for (Node n : nodes) {
      renderNode(n, getNodeColor(n));
    } 
  }
  
  private int getNodeColor(Node n) {
    return n.containsPoint(mouseX, mouseY) ? MOUSED_NODE_COLOR : EMPTY_NODE_COLOR;
  }
  
  private void renderNode(Node n, int c) {
    stroke(c);
    fill(c);
    circle(n.pos, n.radius);
  }
  
  private void circle(Point center, float radius) {
    ellipseMode(RADIUS);
    ellipse(center.x, center.y, radius, radius);
  }
}
