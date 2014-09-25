int numPoints = 20;
Point[] shape;

Point endP;

void setup() {
    size(400, 400);
    smooth();
    shape = new Point[numPoints];
    endP = new Point();

    makeRandomShape();

    frame.setResizable(true);
}

void draw() {
    background(255, 255, 255);
    stroke(0, 0, 0);

    drawShape();
    if (mousePressed == true) {
        stroke(255, 0, 0);
        line(mouseX, mouseY, endP.x, endP.y);

        fill(0, 0, 0);
        boolean isect = isectTest();
        if (isect == true) {
            text("Inside", mouseX, mouseY);
        } else {
            text("Outside", mouseX, mouseY);
        }
    }
}

void mousePressed() {
    endP.x = random(-1, 1) * 2 * width;
    endP.y = random(-1, 1) * 2 * height;
}

void drawShape() {
    for (int i = 0; i < numPoints; i++) {
        int start = i;
        int end = (i + 1) % numPoints;

        line(shape[start].x + width/2.0f, 
             shape[start].y + height/2.0f,
             shape[end].x + width/2.0f, 
             shape[end].y + height/2.0f);
    }
}

boolean isectTest() {
  return !isEven(intersectionCount());
}

boolean isEven(int v) {
  return v % 2 == 0; 
}

int intersectionCount() {
  float y = mouseY - height/2.0f;
  float x = mouseX - width/2.0f;
  
  Point mouse = new Point();
  mouse.x = x;
  mouse.y = y;
  
  int count = 0;
  
  for (int i = 0; i < numPoints; i++) {
    Point start = shape[i];
    
    int next = (i + 1) % numPoints;
    Point end = shape[next];
    
    if (intersects(mouse, start, end)) {
      count++;
    }
  }
  
  println("count = " + count);
  
  return count;
}

boolean intersects(Point mouse, Point start, Point end) {
   if (!isBetween(mouse.x, start.x, end.x)) {
     return false; 
   }
     
   // calculate the y value for mouse.x on the line between start/end
   float percentX = 1 - (mouse.x - end.x) / (start.x - end.x);
   float y = start.y + percentX * (end.y - start.y);
   
   return y <= mouse.y;
}

boolean isBetween(float val, float range1, float range2) {
    println("val = " + val + ", range = [" + range1 + ", " + range2);
  
    float largeNum = range1;
    float smallNum = range2;
    if (smallNum > largeNum) {
        largeNum = range2;
        smallNum = range1;
    }

    if ((val < largeNum) && (val > smallNum)) {
        println(" -- true!!");
        return true;
    }
    println(" -- false!!");
    return false;
}

void makeRandomShape() {
    float slice = 360.0 / (float) numPoints;
    for (int i = 0; i < numPoints; i++) {
        float radius = (float) random(5, 100);
        shape[i] = new Point();
        shape[i].x = radius * cos(radians(slice * i));
        shape[i].y = radius * sin(radians(slice * i));
    }
}
