// main

// constants 
String FILENAME = "hierarchy2.shf";
float STARTING_X = 5;
float STARTING_Y = 5;
float X_OFFSET = 10;
float Y_OFFSET = 10;

// globals
SQTM tm;
Datum root;

void setup() {
  // general canvas setup
  size(600, 800);
  frame.setResizable(true);
  
  // init SQTM
  Rect bounds = new Rect(STARTING_X, STARTING_Y, width - X_OFFSET, height - Y_OFFSET);
  root = new SHFReader(FILENAME).read();
 
  tm = new SQTM(bounds, root);
}


void draw() {
  background(color(255, 255, 255));
  tm.setBounds(new Rect(STARTING_X, STARTING_Y, width - X_OFFSET, height - Y_OFFSET));
  tm.render(); 
}


void mousePressed() {
  if (mouseButton == LEFT) {
    tm.zoomIn(new Point(mouseX, mouseY));
  } else {
    tm.zoomOut();
  }
}

