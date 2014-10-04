// main

// constants 
String FILENAME = "ds1.csv";

Graph current;

void setup() {
  // general canvas setup
  size(900, 600);
  frame.setResizable(true);
  
  frameRate(30);
  
  // init SQTM
  Rect bounds = new Rect(5, 5, width - 10, height - 10);
  
  CSVData data = new CSVReader().read(FILENAME);
  println("root = " + data);
  
  final Bar bg = new Bar(data);
  final PieChart pc = new PieChart(data);
//  final Line lg = new Line(data);
 
// current = pc;
  transition(bg, pc);
  
//  transition(bg, lg);

  background(color(255, 255, 255)); 
  current.render();
}

void transition(final Bar bg, final Line lg) {
  current = animate(bg, lg, new Continuation() {
    public void onContinue() {
      transition(lg, bg); 
    }
  });
}

void transition(final Line lg, final Bar bg) {
  current = animate(lg, bg, new Continuation() {
    public void onContinue() {
      transition(bg, lg); 
    }
  });
}

void transition(final PieChart pc, final Bar bg) {
  current = animate(pc, bg, new Continuation() {
    public void onContinue() {      
      transition(bg, pc); 
    }
  });
}
void transition(final Bar bg, final PieChart pc) {
  current = animate(bg, pc, new Continuation() {
    public void onContinue() {
      transition(pc, bg); 
    }
  });
}

void draw() {
  background(color(255, 255, 255)); 
  current.render();
}



