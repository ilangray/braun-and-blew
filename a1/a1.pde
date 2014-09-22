// main

// constants 
String FILENAME = "hierarchy2.shf";

// globals
SQTM tm;
Datum root;

void setup() {
  // general canvas setup
  size(600, 800);
  frame.setResizable(true);
  
  // init SQTM
  Rect bounds = new Rect(5, 5, width - 10, height - 10);
  root = new Reader(FILENAME).read();
  
  println("root = " + root);
 
  tm = new SQTM(bounds, root);
}


void draw() {
  background(color(255, 255, 255));
  tm = new SQTM(new Rect(5, 5, width - 10, height - 10), root);
  tm.render(); 
  
}


void mousePressed() {
  if (mouseButton == LEFT) {
    tm.zoomIn(new Point(mouseX, mouseY));
  } else {
    tm.zoomOut();
  }
}

