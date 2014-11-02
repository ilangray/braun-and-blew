
String FILENAME = "data_aggregate.csv";
Kontroller kontroller;
NetworkView nv;
boolean done = false;

void setup() {
  size(1000, 600);	
  frame.setResizable(true);

  Rect bounds = new Rect(0, 0, width, height / 3);

  ArrayList<Datum> data = new DerLeser(FILENAME).readIn();
  nv = new NetworkView(data, bounds);
  nv.setBounds(bounds);

  // kontroller = new Kontroller(data);
}

// converts ms to seconds
float seconds(int ms) {
  return ms / 1000.0f; 
}

void draw() {
	nv.render();
  // kontroller.render();
}
