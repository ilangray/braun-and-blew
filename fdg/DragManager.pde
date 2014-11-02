  // the node currently being dragged
  public Node dragged = null;

  Node getNode(int x, int y) {
    for (Node n : nv.fdg.getSimulator().getNodes()) {
      if (n.containsPoint(x, y)) {
        return n;
      } 
    }
    return null;
  }

  void mousePressed() {
    // what did we hit?
    dragged = getNode(mouseX, mouseY);
    
    if (dragged != null) {
      dragged.fixed = true; 
    }
  }

  void mouseDragged() {
    if (dragged != null) {
        if (bounds == null) {
            println("BOUNDS ARE NULL IN DRAG MANAGER");
            System.exit(1);
        }
        float xMin = bounds.x + dragged.radius;
        float xMax = bounds.w + bounds.x - dragged.radius;
        float yMin = bounds.y + dragged.radius;
        float yMax = (bounds.h + bounds.y) - dragged.radius;
      dragged.pos.x = clamp(mouseX, (int)xMin, (int)xMax);
      dragged.pos.y = clamp(mouseY, (int)yMin, (int)yMax);  
    }
  }

  void mouseReleased() {
    if (dragged != null) {
      dragged.fixed = false;
      dragged = null; 
    }
  }