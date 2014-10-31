
Kontroller kontroller;

void setup() {
  size(600, 400);	
  
  ArrayList<Datum> data = new DerLeser("data_aggregate.csv").readIn();
  kontroller = new Kontroller(data);
}


void draw() {
  kontroller.render();
}
