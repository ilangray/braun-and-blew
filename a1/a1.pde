
// main

// constants 
String FILENAME = "data.shf";

// globals
SQTM tm;

void setup() {
  // general canvas setup
  // ...
  
  // init SQTM
  Rect bounds = new Rect(0, 0, width, height);
  Datum root = new Reader(FILENAME).read();
  
  tm = new SQTM(bounds, root);
}

void draw() {
  tm.render();
}

void onClick() {
  // check whether left or right click
  
  // if left: tell the SQTM to zoom IN (and where)
  
  // if right: tell the SQTM to zoom OUT
}
