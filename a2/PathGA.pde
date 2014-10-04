
class PathGA extends GraphAnimator {

  
  public PathGA(PieChart pc, float duration, float percentStart, float percentEnd) {
    super(null, duration, percentStart, percentEnd);
  }
  
  protected Graph.DatumView createDatumView(Datum d, Shape r, float percent) {
//    return bar.createDatumView(d, getScaledRect((Rect)r, percent));
    return null;
  }
}
