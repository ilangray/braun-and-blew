
/**
 * Responsible for rendering the current state of the simulation.
 */
class RenderMachine {
  
  private final int TEXT_SIZE = 14;
  
  private final int EMPTY_NODE_COLOR = color(0,0,0);
  private final int MOUSED_NODE_COLOR = color(0, 255, 0);
  
  private final int SPRING_COLOR = color(0,0,255);
  
  private final ArrayList<Node> nodes;
  private final ArrayList<Spring> springs;
  private final ArrayList<Link> externalLinks;
 
  public RenderMachine(ArrayList<Node> nodes, ArrayList<Spring> springs, ArrayList<Link> externalLinks) {
    this.nodes = nodes;
    this.springs = springs;
    this.externalLinks = externalLinks;
  } 
  
  private void renderExternalLinks(ArrayList<Heatmap> heatmaps) {
    for (Link externalLink : externalLinks) {
      renderExternalLink(externalLink, heatmaps);
    } 
  }
  
  private void renderExternalLink(Link link, ArrayList<Heatmap> heatmaps) {
    Point endA = getLinkEnd(link.authorA, heatmaps);
    Point endB = getLinkEnd(link.authorB, heatmaps);
   
    fill(color(0,0,0));
    stroke(color(0,0,0));
    line(endA.x, endA.y, endB.x, endB.y); 
  }
  
  private Point getLinkEnd(String author, ArrayList<Heatmap> heatmaps) {
    // find the node containing that author
    int index = getNodeByAuthor(author);
    Heatmap hm = heatmaps.get(index);
    return hm.getLabel(author);
  }

  // returns node index
  private int getNodeByAuthor(String author) {
    for (int i = 0; i < nodes.size(); i++) {
      if (nodes.get(i).datum.containsAuthor(author)) {
        return i; 
      }
    }
    
    return -1;
  }

  public void setAllBounds(Rect r) {
    for (Node n : nodes) {
      n.setBounds(r);
    }
  }
  
  public void render() {
    renderSprings();
    ArrayList<Heatmap> hms = renderNodes();
    renderExternalLinks(hms);
    renderLabels();
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
  
  // returns the heatmaps
  private ArrayList<Heatmap> renderNodes() {
    ArrayList<Heatmap> heatmaps = new ArrayList<Heatmap>();
    
    for (Node n : nodes) {
      heatmaps.add(renderNode(n, getNodeColor(n)));
    } 
    
    return heatmaps;
  }
  
  public void renderLabels(){
    for (Node n : nodes) {
      if(n.containsPoint(mouseX, mouseY)) {
        String label = "Id: " + n.id + ", Mass: " + n.mass;
        renderLabel(n.pos, label);
      }
    }
  }
  
  private int getNodeColor(Node n) {
    return n.containsPoint(mouseX, mouseY) ? MOUSED_NODE_COLOR : EMPTY_NODE_COLOR;
  }
  
  // this now uses a heatmap
  private Heatmap renderNode(Node n, int c) {
    Heatmap map = new Heatmap(n.data);
    
    float r = n.radius;
    float x = n.center.x;
    float y = n.center.y;
    
    map.setBounds(new Rect(x - r, y - r, 2*r, 2*r));
    map.render();
    
    return map;
  }
  
  
  private void circle(Point center, float radius) {
    ellipseMode(RADIUS);
    ellipse(center.x, center.y, radius, radius);
  }
  
    // renders the given string as a label above the hitbox
  public void renderLabel(Point p, String s) {
     float x = p.x;
     float y = p.y;
     
     // set font size because text measurements depend on it
     textSize(TEXT_SIZE);
     
     // bounding rectangle
     float w = textWidth(s) * 1.1;
     float h = TEXT_SIZE * 1.3;
     fill(255,255,255, 200);
     noStroke();
     Rect r = new Rect(x - w/2, y - h, w, h);
     rect(r.x, r.y, r.w, r.h, 3);
     
     // text 
     textAlign(CENTER, BOTTOM);
     fill(color(0,0,0));
     text(s, x, y);
   }
}
