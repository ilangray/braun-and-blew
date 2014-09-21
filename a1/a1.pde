// main

// constants 
String FILENAME = "hierarchy2.shf";

// globals
SQTM tm;

void setup() {
  // general canvas setup
  // ...
  
  size(400, 300);
  frame.setResizable(true);
  
  // init SQTM
  Rect bounds = new Rect(5, 5, width - 10, height - 10);
  Datum root = new Reader(FILENAME).read();
  
  println("root = " + root);
 
  tm = new SQTM(bounds, root);
}


void onClick() {
  if (mouseButton == LEFT) {
    tm.zoomIn(new Point(mouseX, mouseY));
  } else {
    tm.zoomOut(); 
  }
}

