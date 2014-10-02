class MyCircle {
  int id;
  int posx, posy, radius;
  int r, g, b;
  boolean selected = false;
  
  MyCircle(int _id, int _posx, int _posy, int _radius) {
    id = _id;
    posx = _posx;
    posy = _posy;
    radius = _radius;
  }
  
  void setColor (int _r, int _g, int _b) {
    r = _r; g = _g; b = _b;
  }
  
  boolean getSelected () {
    return selected;
  }
  
  void setSelected (boolean _selected) {
    selected = _selected;
  }
  
  void render() {
    strokeWeight(5);
    stroke(r, g, b);
    noFill();
    ellipse(posx, posy, radius*2, radius*2);  
  }
  
  void renderIsect(PGraphics pg) {
    pg.fill(red(id), green(id), blue(id));
    pg.stroke(red(id), green(id), blue(id));
    pg.strokeWeight(5);
    pg.ellipse(posx, posy, radius*2, radius*2);  
  }
  
  void renderSelected() {
    strokeWeight(1);
    stroke(r, g, b);
    fill (r, g, b, 128);
    ellipse(posx, posy, radius*2, radius*2);      
  }
  
  boolean isect (PGraphics pg) {

    int act = pg.get(mouseX, mouseY);
    
    float actR = red(act);
    float actG = green(act);
    float actB = blue(act);
    
    float expR = red(id);
    float expG = green(id); 
    float expB = blue(id);
    
    println("exp = " + expR + ", " + expG + ", " + expB + ", act = " + actR + ", " + actG + ", " + actB); 
    
    return actR == expR && actG == expG && actB == expB;
  }
}
