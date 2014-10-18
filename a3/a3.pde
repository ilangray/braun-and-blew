
// this is awesome. lets do some physics yolo swag

void setup() {
  Configurator c = new Configurator("data.csv");
  DieWelt w = c.configure();
/*  
  for (int i = 0; i < w.zaps.size(); i++) {
    println(w.zaps.get(i).endA.id + ", " + w.zaps.get(i).endB.id);
  }
  */
  
  println("num Zaps =", w.zaps.size());
}

void draw() {
}

