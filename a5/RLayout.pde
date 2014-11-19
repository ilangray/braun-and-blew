
// owned by phil
class RLayout {
  private class Algorithm {
    private final int INSET_AMOUNT = 2;
    View parent;  // Parent view
    private Point realUL;  // The actual coordinate of the UL point of canvas
    private float sumOfRects;  // Sum of rects to be placed
    private float scale;  // To scale up to dimenssions of the screen
    private float vWidth;
    private float vHeight;

    // The floats are the unscaled area
    private ArrayList<SQTMDatum> remRects;  // Rectangles to be placed
    private ArrayList<Rect> currentRects;  // Rectangles in current row
    private ArrayList<SQTMDatum> currentDatums;  // Parallel arrays with currentRects corresponding Datums
    private ArrayList<View> finalViews;  // Final view for the level -- exported by class


    public Algorithm(View parent, float vWidth, float vHeight, ArrayList<SQTMDatum> startingVals, Point realUL) {
      this.parent = parent;
      this.remRects = startingVals;
      this.currentRects = null;
      this.currentDatums = null;
      this.finalViews = new ArrayList();
      this.realUL = realUL;
      this.sumOfRects = 0;
      this.scale = 0;
      this.vWidth = vWidth;
      this.vHeight = vHeight;
    }

    private void squarify(boolean segmentsAreVertical) {
      if (remRects == null || remRects.isEmpty()) {
        return;
      }

      if (segmentsAreVertical) {
        placeVerticalSegments();
      } else {
        placeHorizontalSegments();
      }
    }


    private void placeVerticalSegments() {
      sumOfRects = getSumOfRects();
      scale = vWidth * vHeight / sumOfRects;
      float rectHeight = vHeight; // Shared side
      float xCoord = realUL.x;
      float yCoord = realUL.y;

      // Place all the rectangles
      float widthUsed = 0;
      while (!remRects.isEmpty()) {
        SQTMDatum d = getLargestRemaining();
        remRects.remove(getLargestRemaining());  // Take it out of remRects
        float scaledArea = d.value * scale;
        float rectWidth = scaledArea / rectHeight;
        Rect r = new Rect(xCoord + widthUsed, yCoord, vWidth - widthUsed, vHeight);
        finalViews.add(new View(d, r));

        widthUsed += rectWidth;
      }

    }

    private void placeHorizontalSegments() {
      sumOfRects = getSumOfRects();
      scale = vWidth * vHeight / sumOfRects;

      float rectWidth = vWidth;
      float xCoord = realUL.x;
      float yCoord = realUL.y;

      float heightUsed = 0;

      while (!remRects.isEmpty()) {
        SQTMDatum d = getLargestRemaining();
        remRects.remove(getLargestRemaining());
        float scaledArea = d.value * scale;
        float rectHeight = scaledArea / rectWidth;

        Rect r = new Rect(xCoord, yCoord + heightUsed, vWidth, vHeight - heightUsed);

        finalViews.add(new View(d, r));

        heightUsed += rectHeight;
      }
    }

    private SQTMDatum getLargestRemaining() {
      SQTMDatum max = remRects.get(0);
      SQTMDatum current = null;
      for (int i = 1; i < remRects.size (); i++) {
        current = remRects.get(i);
        if (current.value > max.value) {
          max = current;
        }
      }
      return max;
    }

    // Cannot be called on empty remRects array
    // NOTE: remRects is an ArrayList of Numbers
    private int getIndexLargestRemaining() {
      float max = remRects.get(0).getValueF();
      int maxInd = 0;

      for (int i = 1; i < remRects.size (); i++) {
        float current = remRects.get(i).getValueF();
        if (current > max) {
          max = current;
          maxInd = i;
        }
      }

      return maxInd;
    }

    private float getSumOfRects() {
      float sum = 0;
      for (int i = 0; i < remRects.size (); i++) {
        sum += remRects.get(i).getValueF();
      }
      return sum;
    }
  }

  public final SQTMDatum root;
  public final Rect bounds;

  public RLayout(SQTMDatum root, Rect bounds) {
    this.root = root;
    this.bounds = bounds;
  }

  private ArrayList<SQTMDatum> copy(ArrayList<SQTMDatum> ds) {
    if (ds == null) {
      return null;
    }
    
    return new ArrayList<SQTMDatum>(ds);
  }

  private View recurSolve(View node, boolean segmentsAreVertical) {
    // Boolean shortIsWidth = node.bounds.h > node.bounds.w;
    float vWidth = node.bounds.w;
    float vHeight = node.bounds.h;
    //This part used to assume that the width was always the long side of the view
    Algorithm a = new Algorithm(node, vWidth, vHeight, copy(node.datum.children), new Point(node.bounds.x, node.bounds.y));
    a.squarify(segmentsAreVertical);
    
    for (View v : a.finalViews) {
      node.subviews.add(v);
    }
    
    for (View v : node.subviews) {
      recurSolve(v, !segmentsAreVertical);
    }
    
    return node;
  }
  
  public View solve() {
    View viewRoot = new View(root, bounds);
    if(root.children != null && !root.children.isEmpty()) {
      recurSolve(viewRoot, true);  // Segments start vertical
    }
    return viewRoot;
  }
}

