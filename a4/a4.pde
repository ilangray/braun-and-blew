
String FILENAME = "data_aggregate.csv";
Kontroller kontroller;
NetworkView nv;
boolean done = false;

void setup() {
	size(1000, 600);	
	frame.setResizable(true);

	ArrayList<Datum> data = new DerLeser(FILENAME).readIn();
	kontroller = new Kontroller(data);
}

// converts ms to seconds
float seconds(int ms) {
  return ms / 1000.0f; 
}

void draw() {
  	kontroller.render();
}
