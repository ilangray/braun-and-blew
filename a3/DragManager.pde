
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
    dragged.pos.x = mouseX;
    dragged.pos.y = mouseY;  
  }
}

void mouseReleased() {
  if (dragged != null) {
    dragged.fixed = false;
    dragged = null; 
  }
}

