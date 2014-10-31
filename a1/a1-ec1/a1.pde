// main

// constants 
String FILENAME = "soe-funding.csv";
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
  ArrayList<Entry> es = new CSVReader().read(FILENAME);
  ArrayList<Property> ps = new ArrayList<Property>();
  ps.add(Property.DEPT);
  ps.add(Property.SPONSOR);
  ps.add(Property.YEAR);
  
  root = new Transmogifier().groupBy(es, ps);
  println("root = " + root);
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

