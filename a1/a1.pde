
// main

// constants 
String FILENAME = "hierarchy2.shf";

// globals
SQTM tm;

void setup() {
  // general canvas setup
  // ...
  
  // init SQTM
  Rect bounds = new Rect(0, 0, width, height);
  Datum root = new Reader(FILENAME).read();
  
  println("root = " + root);
  
  tm = new SQTM(bounds, root);
}

void draw() {
  tm.render();
}

void onClick() {
  if (mouseButton == LEFT) {
    tm.zoomIn(new Point(mouseX, mouseY));
  } else {
    tm.zoomOut(); 
  }
}
