
String FILENAME = "data_aggregate.csv";
Kontroller kontroller;
NetworkView nv;
boolean done = false;

ArrayList<String> MODES = makeList("Hover", "Or");
int currentMode = 0;

void setup() {
	size(1000, 600);	
	frame.setResizable(true);

	ArrayList<Datum> data = new DerLeser(FILENAME).readIn();
	kontroller = new Kontroller(data);

	kontroller.setSelectionController(MODES.get(currentMode));
}

// converts ms to seconds
float seconds(int ms) {
  return ms / 1000.0f; 
}

void draw() {
  	kontroller.render();

  	textAlign(CENTER, BOTTOM);
  	textSize(20);
  	fill(color(0,0,0));
  	stroke(color(0,0,0));
  	text(MODES.get(currentMode), 0.875 * width, 0.05 * height);
}

void mousePressed() { 
	kontroller.getMouseHandler().mousePressed();
}

void mouseDragged() { 
	kontroller.getMouseHandler().mouseDragged();
}

void mouseReleased() { 
	kontroller.getMouseHandler().mouseReleased();
}

void mouseClicked() { 
	kontroller.getMouseHandler().mouseClicked();

	// if hits button, rotate
}

void keyPressed() {
	currentMode = (currentMode + 1) % MODES.size();
	kontroller.setSelectionController(MODES.get(currentMode));
}
