
String FILENAME = "data_aggregate.csv";
Kontroller kontroller;

void setup() {
  size(1000, 600);	
  frame.setResizable(true);
  
  ArrayList<Datum> data = new DerLeser(FILENAME).readIn();
  kontroller = new Kontroller(data);
}

void draw() {
  kontroller.render();
}
