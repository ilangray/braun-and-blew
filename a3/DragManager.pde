
// the node currently being dragged
Node dragged = null;

Node getNode(int x, int y) {
  for (Node n : sm.getNodes()) {
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
    float r = dragged.radius;
    dragged.pos.x = clamp(mouseX, (int)r, (int)(width - r));
    dragged.pos.y = clamp(mouseY, (int)r, (int)(height - r));  
  }
}

void mouseReleased() {
  if (dragged != null) {
    dragged.fixed = false;
    dragged = null; 
  }
}

