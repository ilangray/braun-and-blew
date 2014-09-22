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
int indORDERS = 0;
ArrayList<ArrayList<Property>> ORDERS = makeList(
  makeList(Property.DEPT, Property.SPONSOR, Property.YEAR),
  makeList(Property.DEPT, Property.YEAR, Property.SPONSOR),
  makeList(Property.SPONSOR, Property.DEPT, Property.YEAR),
  makeList(Property.SPONSOR, Property.YEAR, Property.DEPT),
  makeList(Property.YEAR, Property.DEPT, Property.SPONSOR),
  makeList(Property.YEAR, Property.SPONSOR, Property.DEPT)
);
ArrayList<Entry> ENTRIES = null; 

Graph g; 
Button toggle;

void setup() {
  // general canvas setup
  size(600, 800);
  toggle = new Button(new Rect(0, 0, 60, 30), color(0, 0, 0), "Toggle", color(255, 255, 255));
  frame.setResizable(true);
  ENTRIES = new CSVReader().read(FILENAME);
}

<T> ArrayList<T> makeList(T... ts) {
  ArrayList<T> toReturn = new ArrayList();
  
  for (T t : ts) {
    toReturn.add(t);
  }
  
  return toReturn;
}


void draw() {
  background(color(255, 255, 255));
  ArrayList<GDatum> gds = new Transmogifier().groupBy(ENTRIES, ORDERS.get(indORDERS));
  g = new SQTMBar(gds, ORDERS.get(indORDERS).get(0).name, "Funding");
  g.render();
  toggle.render();
}

/*
void mousePressed() {
  if (mouseButton == LEFT) {
    tm.zoomIn(new Point(mouseX, mouseY));
  } else {
    tm.zoomOut();
  }
}
*/

void mouseClicked() {
  if (toggle.frame.containsPoint(mouseX, mouseY)){
    indORDERS = (1 + indORDERS) % ORDERS.size();
  }

}
