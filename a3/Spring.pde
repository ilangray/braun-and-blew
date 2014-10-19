// This is what you think it is
class Spring extends InterNodeForce {
  
  private static final float K = 0.5f;
  
  public final double restLen;
  
  public Spring(Node endA, Node endB, double restLen) {
    super(endA, endB);
    this.restLen = restLen;
  }
  
  public void applyForce() {
    // force is proportional to the diff between restLen and current idst 
    println("restlen = " + restLen + ", curr dist = " + getDistance()); 
     
    Vector diff = new Vector(endA.pos, endB.pos);
    
    // ensure that the diff's mag is > 1
    if (diff.getMagnitude() < 1) {
      println(" __________ NORMALIZED TO GET MAG UP TO 1 _________________");
      diff.normalize(); 
    }
    
    Vector force = diff.scale(-K, -K);
    
    println("magnitude of spring force = " + force.getMagnitude());
    
    if (restLen < getDistance()) {
      println("INWARDS");
      // forces go INWARDS 
      endB.addForce(force);
      endA.addForce(force.reverse());  
    } else {
      println("OUTWARDS");
      // forces go OUTWARDS
      endA.addForce(force);
      endB.addForce(force.reverse());
    }
  }
}
