
// owned by phil
class Layout {
  private class Algorithm {
    View parent;  // Parent view
    private Point realUL;  // The actual coordinate of the UL point of canvas
    private float canvLong;  // Length of long side of canvas
    private float canvShort;  // Length of short side of canvas
    private float sumOfRects;  // Sum of rects to be placed
    private float scale;  // To scale up to dimenssions of the screen
    private Boolean shortIsWidth;  // Which side is the short side?
    // TODO: Change to boolean

    // The floats are the unscaled area
    private ArrayList<Datum> remRects;  // Rectangles to be placed
    private ArrayList<Rect> currentRects;  // Rectangles in current row
    private ArrayList<Datum> currentDatums;  // Parallel arrays with currentRects corresponding Datums
    private ArrayList<View> finalViews;  // Final view for the level -- exported by class


    public Algorithm(View parent, float canvLong, float canvShort, ArrayList<Datum> startingVals, Point realUL, Boolean shortIsWidth) {
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
      if (remRects.isEmpty()) {
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
        Datum nodeToConsider = getLargestRemaining();
        float areaCurrent = nodeToConsider.getValueF() * scale;

        // Calculate length of shared long side
        float sharedLong = 0;
        for (int i = 0; i < currentRects.size (); i++) {
          sharedLong += currentRects.get(i).getArea();
        }

        sharedLong += areaCurrent;
        sharedLong /= canvShort;
//        println("Shared Long");
//        println(sharedLong);

        ArrayList<Rect> tempRects = new ArrayList();
        ArrayList<Datum> tempDatums = new ArrayList();
        float shortAreaUsedUp = 0;  // Used for placing the rectangles
        // Put in previous rectangles
        for (int i = 0; i < currentRects.size (); i++) {
          Rect cRect = currentRects.get(i);
          Datum toAdd = currentDatums.get(i);
          shortAreaUsedUp += addNewRectToTemp(tempRects, cRect.getArea(), tempDatums, toAdd, sharedLong, shortAreaUsedUp);
        }
        addNewRectToTemp(tempRects, areaCurrent, tempDatums, nodeToConsider, sharedLong, shortAreaUsedUp); 
        float newWorstAR = getWorstAR(tempRects);
//        println("TEMPARR");
//        testPrintRectArray(tempRects);
//        println("END");
        
        print("New Worst AR: ");
        println(newWorstAR);
        print("Old Worst AR: ");
        println(oldWorstAR);
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
//        println("shortIsWidth");
        float longShared = currentRects.get(0).h;
        realUL = realUL.offset(new Point(0, longShared));
        canvLong -= longShared;
      } else {
//        println("Long is width");
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
//      print("New UL: (");
//      print(realUL.x);
//      print(", ");
//      print(realUL.y);
//      println(")");

      // Recurse!
      addViews();
      squarify();
    }

    private void addViews() {
      for (int i = 0; i < currentRects.size (); i++) {
        Datum toAddDatum = currentDatums.get(i);
        Rect toAddRect = currentRects.get(i);
        View toAddView = new View(toAddDatum, toAddRect);
        finalViews.add(toAddView);
      }
    }

    // Adds largest datum to currentRect and currentDatum
    private void addFirstRect() {
      Datum toAdd = getLargestRemaining();
      float areaCurrent = toAdd.getValueF();
      float longCurrent = areaCurrent / canvShort * scale;

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
    private float addNewRectToTemp(ArrayList<Rect> tempRects, float areaRect, ArrayList<Datum> tempDatums, Datum toAddDatum, float sharedLong, float shortAreaUsedUp) {
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
    
//    private void testPrintRectArray(ArrayList<Rect> a) {
//      for (int i = 0; i < a.size(); i++) {
//        println(a.get(i).toString());
//      }
//    }

    private Datum getLargestRemaining() {
      Datum max = remRects.get(0);
      Datum current = null;
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

  public final Datum root;
  public int numLevels;  // Used to know how many times to instantiate an algorithm


  public Layout(Datum root) {
    this.root = root;
    this.numLevels = findLongestPath(root);
  }

  public View solve() {
    View viewRoot = new View(root, new Rect(0, 0, width, height));
    for (int i = 0; i < (numLevels - 1); i++) {
      Boolean shortIsWidth = height > width ? true : false; 
      Algorithm a = new Algorithm(viewRoot, float(width), float(height), getChildren(i), new Point(0, 0), shortIsWidth);
      a.squarify();
      
      for (View v : a.finalViews) {
        viewRoot.subviews.add(v);
      }
      
    }

    return null;
  }

  public void testPrintNumArray(ArrayList<Number> arr) {
    for (int i = 0; i < arr.size (); i++) {
      println(arr.get(i).floatValue());
    }
  }


  // Gets all children whose parents are level 
  private ArrayList<Datum> getChildren(int level) {
    ArrayList<Datum> saveList = new ArrayList();
    recurGetChildren(root, 0, level + 1, saveList);
    return saveList;
  }

  // Cannot be called on null pointer
  // 10Q: http://stackoverflow.com/questions/13349853/find-all-nodes-in-a-binary-tree-on-a-specific-level-interview-query
  private void recurGetChildren(Datum node, int currentLev, int targetLev, ArrayList<Datum> saveList) {
    // Target case
    if (currentLev == targetLev) {
      saveList.add(node);
    }

    // Base case
    if (node.children == null) {
      return;
    }

    // Recursion case
    for (int i = 0; i < node.children.size(); i++) {
      recurGetChildren(node.children.get(i), currentLev + 1, targetLev, saveList);
    }
  }



  private int findLongestPath(Datum node) {
    // Base cases
    if (node == null) {
      return 0;
    }
    if (node.children == null) {
      return 1;
    }

    int maxPath = 0;
    int current;
    for (int i = 0; i < root.children.size (); i++) {
      current = 1 + findLongestPath(root.children.get(i));

      if (current > maxPath) {
        maxPath = current;
      }
    }

    return maxPath;
  }

  public View layout() {
    return null;
  }
}

