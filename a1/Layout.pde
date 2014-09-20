
// owned by phil
class Layout {
  private class Algorithm {
    View parent;  // Parent view
    private Point realUL;  // The actual coordinate of the UL point of canvas
    private float worstAR;  // Current worst aspect ratio
    private float canvLong;  // Length of long side of canvas
    private float canvShort;  // Length of short side of canvas
    
    // The floats are the unscaled area
    private ArrayList<Number> remRects;  // Rectangles to be placed
    
    private ArrayList<Rect> currentRects;  // Rectangles in current row
    private ArrayList<View> finalViews;  // Final view for the level -- exported by class
    
    
    public Algorithm(View parent, float canvLong, float canvShort, ArrayList<Number> startingVals){
      this.parent = parent;
      this.canvLong = canvLong;
      this.canvShort = canvShort;
      this.remRects = startingVals;
    }
  }
}
