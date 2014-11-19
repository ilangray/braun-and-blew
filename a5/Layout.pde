
// owned by phil
class Layout {
  private class Algorithm {
    private final int INSET_AMOUNT = 2;
    View parent;  // Parent view
    private Point realUL;  // The actual coordinate of the UL point of canvas
    private float canvLong;  // Length of long side of canvas
    private float canvShort;  // Length of short side of canvas
    private float sumOfRects;  // Sum of rects to be placed
    private float scale;  // To scale up to dimenssions of the screen
    private boolean shortIsWidth;  // Which side is the short side?

    // The floats are the unscaled area
    private ArrayList<SQTMDatum> remRects;  // Rectangles to be placed
    private ArrayList<Rect> currentRects;  // Rectangles in current row
    private ArrayList<SQTMDatum> currentDatums;  // Parallel arrays with currentRects corresponding Datums
    private ArrayList<View> finalViews;  // Final view for the level -- exported by class


    public Algorithm(View parent, float canvLong, float canvShort, ArrayList<SQTMDatum> startingVals, Point realUL, boolean shortIsWidth) {
      this.parent = parent;
      this.canvLong = canvLong;
      this.canvShort = canvShort;
      this.remRects = startingVals;
      this.currentRects = null;
      this.currentDatums = null;
      this.finalViews = new ArrayList();
      this.realUL = realUL;
      this.sumOfRects = 0;
      this.scale = 0;
      this.shortIsWidth = shortIsWidth;
    }

    private void squarify() {
      // A Base case -- no remaining rects to place
      if (remRects == null || remRects.isEmpty()) {
        return;
      }

      // Need clean list every time
      currentRects = new ArrayList();
      currentDatums = new ArrayList();

      sumOfRects = getSumOfRects();
      scale = canvShort * canvLong / sumOfRects;

      addFirstRect();
      // Rect added, so need to delete it from remaining rects
      remRects.remove(getIndexLargestRemaining());

      // Loop invariant: All rectangles already in remRects have been scaled
      // to fit the screen
      while (!remRects.isEmpty ()) {
        float oldWorstAR = getWorstAR(currentRects);
        
        // Get next rectangle
        SQTMDatum nodeToConsider = getLargestRemaining();
        float areaCurrent = nodeToConsider.getValueF() * scale;

        // Calculate length of shared long side
        float sharedLong = 0;
        for (int i = 0; i < currentRects.size (); i++) {
          sharedLong += currentRects.get(i).getArea();
        }

        sharedLong += areaCurrent;
        sharedLong /= canvShort;
        
        ArrayList<Rect> tempRects = new ArrayList();
        ArrayList<SQTMDatum> tempDatums = new ArrayList();
        float shortAreaUsedUp = 0;  // Used for placing the rectangles
        // Put in previous rectangles
        for (int i = 0; i < currentRects.size (); i++) {
          Rect cRect = currentRects.get(i);
          SQTMDatum toAdd = currentDatums.get(i);
          shortAreaUsedUp += addNewRectToTemp(tempRects, cRect.getArea(), tempDatums, toAdd, sharedLong, shortAreaUsedUp);
        }
        addNewRectToTemp(tempRects, areaCurrent, tempDatums, nodeToConsider, sharedLong, shortAreaUsedUp); 
        float newWorstAR = getWorstAR(tempRects);

        // If next rectangle makes things worse, then GTFO
        if (newWorstAR >= oldWorstAR) {
          break;
          
        }       
        // If next rectange would improve aspect ratio, add it in
        currentRects = tempRects;  // Yah Garbage collection
        currentDatums = tempDatums;
        // Need to remove the largest one
        remRects.remove(getIndexLargestRemaining());
      } 
      // Update pointUL and longSide of the canvas
      // Update canvas dims
      if (shortIsWidth) {
        float longShared = currentRects.get(0).h;
        realUL = realUL.offset(new Point(0, longShared));
        canvLong -= longShared;
      } else {
        float longShared = currentRects.get(0).w;
        realUL = realUL.offset(new Point(longShared, 0));
        canvLong -= longShared;
      }

      // Check if the short and long sides have swapped
      if  (canvLong < canvShort) {  
        shortIsWidth = !shortIsWidth;
        float temp = canvLong;
        canvLong = canvShort;
        canvShort = temp;
      } 
      
      // Recurse!
      addViews();
      squarify();
    }

    private void addViews() {
      for (int i = 0; i < currentRects.size (); i++) {
        SQTMDatum toAddDatum = currentDatums.get(i);
        Rect toAddRect = currentRects.get(i).inset(INSET_AMOUNT);
        View toAddView = new View(toAddDatum, toAddRect);
        finalViews.add(toAddView);
      }
    }

    // Adds largest datum to currentRect and currentDatum
    private void addFirstRect() {
      SQTMDatum toAdd = getLargestRemaining();
      float areaCurrent = toAdd.getValueF() * scale;
      float longCurrent = areaCurrent / canvShort;
      // Added extra multiplication by scale cause 2D

      // Short side isn't necessarily width or height, constructing rectangle changes based on this
      // NOTE: rectange added in is already scaled
      if (shortIsWidth) {
        currentRects.add(new Rect(realUL.x, realUL.y, canvShort, longCurrent));  
      } else {   // The height is the short side
        currentRects.add(new Rect(realUL.x, realUL.y, longCurrent, canvShort));
      }
      currentDatums.add(toAdd);
    }

    // Returns the worst aspect ratio in an ArayList of Rects
    // Cann't be called on empty ArrayList  -- it will explode
    private float getWorstAR(ArrayList<Rect> inputList) {
      float worstAR = inputList.get(0).getAspectRatio();

      for (int i = 1; i < inputList.size (); i++) {
        float currentAR = inputList.get(i).getAspectRatio();
        if (currentAR > worstAR) {
          worstAR = currentAR;
        }
      }

      return worstAR;
    }

    // Adds in a newRect and returns the amount of the short side that was used
    private float addNewRectToTemp(ArrayList<Rect> tempRects, float areaRect, ArrayList<SQTMDatum> tempDatums, SQTMDatum toAddDatum, float sharedLong, float shortAreaUsedUp) {
      // Area of rectangle has already been scaled
      float shortSideRect = areaRect / sharedLong;

      Rect newRect;
      if (shortIsWidth) {
        float xCoord = realUL.x + shortAreaUsedUp;
        float yCoord = realUL.y;
        float rectWidth = shortSideRect;
        float rectHeight = sharedLong;
        newRect = new Rect(xCoord, yCoord, rectWidth, rectHeight);
      } else {  // Short side is the height
        float xCoord = realUL.x;
        float yCoord = realUL.y + shortAreaUsedUp;
        float rectWidth = sharedLong;
        float rectHeight = shortSideRect;
        newRect = new Rect(xCoord, yCoord, rectWidth, rectHeight);
      }
      tempDatums.add(toAddDatum);

      tempRects.add(newRect);

      return shortSideRect;
    }

    private SQTMDatum getLargestRemaining() {
      SQTMDatum max = remRects.get(0);
      SQTMDatum current = null;
      for (int i = 1; i < remRects.size (); i++) {
        current = remRects.get(i);
        if (current.getValueF() > max.getValueF()) {
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

  public Layout(SQTMDatum root, Rect bounds) {
    this.root = root;
    this.bounds = bounds;
  }

  private ArrayList<SQTMDatum> copy(ArrayList<SQTMDatum> ds) {
    if (ds == null) {
      return null;
    }
    
    return new ArrayList<SQTMDatum>(ds);
  }

  private View recurSolve(View node) {
    boolean shortIsWidth = node.bounds.h > node.bounds.w;
    float canvShort = shortIsWidth ? node.bounds.w : node.bounds.h;     
    float canvLong = shortIsWidth ? node.bounds.h : node.bounds.w;
    //This part used to assume that the width was always the long side of the view
    Algorithm a = new Algorithm(node, canvLong, canvShort, copy(node.datum.children), new Point(node.bounds.x, node.bounds.y), shortIsWidth);
    a.squarify();
    
    for (View v : a.finalViews) {
      node.subviews.add(v);
    }
    
    for (View v : node.subviews) {
      recurSolve(v);
    }
    
    return node;
  }
  
  public View solve() {
    View viewRoot = new View(root, new Rect(bounds.x, bounds.y, bounds.w, bounds.h));
    if(root.children != null && !root.children.isEmpty()) {
      recurSolve(viewRoot);
    }
    return viewRoot;
  }
  
  public void printTree(View node) {
    print(node.datum.id);
    print(": ");
    println(node.bounds.toString());
    
    for (View v : node.subviews) {
      printTree(v); 
    }
  }

  public void testPrintNumArray(ArrayList<Number> arr) {
    for (int i = 0; i < arr.size (); i++) {
      println(arr.get(i).floatValue());
    }
  }
}

