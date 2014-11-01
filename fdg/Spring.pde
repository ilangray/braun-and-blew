// This is what you think it is
class Spring extends InterNodeForce {
  
  private static final float K = 2f;
  // private static final float K = 0f;
  
  public final float restLen;
  
  public Spring(Node endA, Node endB, float restLen) {
    super(endA, endB);
    this.restLen = restLen;
  }
  
  public void applyForce() {
    // force is proportional to the diff between restLen and current idst 
//    println("restlen = " + restLen + ", curr dist = " + getDistance()); 
    
    // a vector from A --> B
    Vector diff = new Vector(endA.pos, endB.pos);
    
    // compute the current distance
    float dist = diff.getMagnitude();
    // compute dx, which is what the force depends on
    float dx = Math.abs(dist - restLen);
    
    // a vector containing just the direction component of A --> B 
    Vector dir = diff.copy().normalize();
    
    // ensure that the diff's mag is > 1
//    if (diff.getMagnitude() < 1) {
//      println(" __________ NORMALIZED TO GET MAG UP TO 1 _________________");
//      diff.normalize(); 
//    }
    
//    Vector force = diff.copy().scale(-K, -K);

    Vector force = dir.copy().scale(-K * dx, -K * dx);    
//    println("spring btwn = [" + endA.id + ", " + endB.id + "], dist = " + dist + ", dx = " + dx + ", force = " + force + ", force mag = " + force.getMagnitude());
    
    if (restLen < getDistance()) {
      // forces go INWARDS
      
      endB.addForce(force);
      endA.addForce(force.reverse());  
    } else {
      // forces go OUTWARDS
      
      endA.addForce(force);
      endB.addForce(force.reverse());
    }
  }

  public String toString() {
    return "Node 1 = " + endA.id + ", Node 2 = " + endB.id + ", restLen = "
      + restLen + ";";
  }

}