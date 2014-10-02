
// :)
class Datum {
  public final String key;
  public final float value;
  
  public Datum(String key, float value) {
    this.key = key;
    this.value = value;
  }
  
  public String toString() {
    return "Datum{key = " + key + ", value = " + value + "}"; 
  }
}
