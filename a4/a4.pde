
Kontroller kontroller;

void setup() {
  size(600, 400);	
  
  ArrayList<Datum> data = new ArrayList<Datum>();
  kontroller = new Kontroller(data);
}


void draw() {
  kontroller.render();
}
