
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
    dragged.pos.x = clamp(mouseX, 0, width);
    dragged.pos.y = clamp(mouseY, 0, height);  
  }
}

void mouseReleased() {
  if (dragged != null) {
    dragged.fixed = false;
    dragged = null; 
  }
}

