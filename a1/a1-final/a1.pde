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
  size(1000, 800);

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
  float butWidth = g.PADDING_RIGHT / 1.2 * width;
  float butHeight = g.PADDING_TOP / 2.5 * height;
  float offset = butWidth / 10;
  
  toggle = new Button(new Rect(width - butWidth - offset, offset, butWidth, butHeight), color(0, 0, 0), "Toggle", color(255, 255, 255));
  toggle.render();
  
  // render the current order of properties
  textSize(15);
  textAlign(CENTER, CENTER);
  fill(color(0, 0, 0));
  text(ORDERS.get(indORDERS).toString(), width/2, offset + butHeight/2);
}

void mouseClicked() {
  if (toggle.frame.containsPoint(mouseX, mouseY)){
    indORDERS = (1 + indORDERS) % ORDERS.size();
  }
}
