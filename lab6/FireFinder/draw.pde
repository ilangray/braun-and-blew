int canvasWidth = MIN_INT; // this would be initialized in setup

void draw() {
  clearCanvas();
//  fill(color(255,255,255));
//  rect(0, 0, canvasWidth, height);
  
  stroke(color(0,0,0));
  fill(color(0,0,0));
  
  Scatter2 scat = new Scatter2(points, canvasWidth, height, minX, maxX, minY, maxY);
  scat.render();  
}
