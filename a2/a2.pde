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
  
  colorize(data.datums);
  
  final Bar bg = new Bar(data);                  // tested
  final PieChart pc = new PieChart(data);        // tested
  final Line lg = new Line(data);                // tested
  final HeightGraph hg = new HeightGraph(data);  // tested
  final Scatterplot sp = new Scatterplot(data);  // tested
  final PathGraph pg = new PathGraph(pc, null);  // tested
 
//  current = pg;
//  transition(bg, pc);
//  transition(bg, lg);

   current = animate(lg, pc, new Continuation() {
     public void onContinue() {
       current = pc; 
     }
   });

//  background(color(255, 255, 255)); 
//  current.render();
}

void colorize(ArrayList<Datum> ds) {
  color start = color(202, 232, 211);
  color end = color(3, 101, 152);
  
  for (int i = 0; i < ds.size(); i++) {
    Datum datum = ds.get(i);
    float percent = 1.0 / ds.size() * i;
    
    float r = lerp(red(start), red(end), percent);
    float g = lerp(green(start), green(end), percent);
    float b = lerp(blue(start), blue(end), percent);
    
    datum.fillColor = color(r, g, b);
  } 
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



