
// owned by phil
class Layout {
  private class Algorithm {
    View parent;  // Parent view
    private Point realUL;  // The actual coordinate of the UL point of canvas
    private float worstAR;  // Current worst aspect ratio
    private float canvLong;  // Length of long side of canvas
    private float canvShort;  // Length of short side of canvas
    private float sumOfRects;  // Sum of rects to be placed
    private float scale;  // To scale up to dimenssions of the screen
    
    // The floats are the unscaled area
    private ArrayList<Number> remRects;  // Rectangles to be placed
    
    private ArrayList<Rect> currentRects;  // Rectangles in current row
    private ArrayList<View> finalViews;  // Final view for the level -- exported by class
    
    
    public Algorithm(View parent, float canvLong, float canvShort, ArrayList<Number> startingVals, Point realUL){
      this.parent = parent;
      this.canvLong = canvLong;
      this.canvShort = canvShort;
      this.remRects = startingVals;
      this.currentRects = new ArrayList();
      this.finalViews = new ArrayList();
      this.realUL = realUL;
      this.sumOfRects = getSumOfRects();
      this.scale = canvShort * canvLong / this.sumOfRects;
    }
    
    private float getSumOfRects() {
      float sum = 0;
      for (int i = 0; i < remRects.size(); i++) {
        sum += remRects.get(i).floatValue();
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
     for (int i = 0;  i < (numLevels - 1); i++) {
       Algorithm a = new Algorithm(new View(root, null, null), width, height, getChildren(i), new Point(0, 0));
       testPrintNumArray(a.remRects);
     }
     
     return null;
  }
  
  public void testPrintNumArray(ArrayList<Number> arr) {
    for (int i = 0; i < arr.size(); i++) {
      println(arr.get(i).floatValue());
    }
  }
  
  
  // Gets all children whose parents are level 
  private ArrayList<Number> getChildren(int level) {
    ArrayList<Number> saveList = new ArrayList();
    recurGetChildren(root, 0, level + 1, saveList);
    return saveList;
  }
  
  // Cannot be called on null pointer
  // 10Q: http://stackoverflow.com/questions/13349853/find-all-nodes-in-a-binary-tree-on-a-specific-level-interview-query
  private void recurGetChildren(Datum node, int currentLev, int targetLev, ArrayList<Number> saveList) {
    // Target case
    if (currentLev == targetLev) {
      Number toAdd = node.value;
      saveList.add(toAdd);
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
    for (int i = 0; i < root.children.size(); i++) {
      current = 1 + findLongestPath(root.children.get(i));
      
      if (current > maxPath) {
        maxPath = current;
      }
    }
    
    return maxPath;
    
  }
  
  public View layout(){
    return null;
  }
  
}
