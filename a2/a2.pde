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
//  final HeightGraph hg = new HeightGraph(data);
//  final Scatterplot scat = new Scatterplot(data);
  final Line lg = new Line(data);
  
  /*
  current = animate(lg, bg, new Continuation() {
    public void onContinue() {
      println("YOLO");
      current = bg;
      
      current = animate(bg, lg, new Continuation() {
        public void onContinue() {
          current = lg;
        }
      });
    }
  });
  */
  
  transition(bg, lg);
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

void draw() {
  background(color(255, 255, 255)); 
  current.render();
}

