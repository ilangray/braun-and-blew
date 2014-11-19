
class Datum {
  public final String key = "";
  public final float value;
  public final boolean marked;
  public final int fillColor = color(0, 255, 255);

  public Datum(Data.DataPoint dp) {
    this.value = dp.value; 
    marked = dp.marked;
  }
}

