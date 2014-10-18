// This is what you think it is
class Spring extends BaseSource {
  
  public static final float K = 0.5f;
  
  public final float restLen;
  
  public Spring(Node endA, Node endB, float k, float restLen) {
    super(endA, endB);
    this.restLen = restLen;
  }
  
  public void applyForce() {
    // force is proportional to the diff between restLen and current idst 
    float diffstance = restLen - getDistance();
     
    Vector diff = new Vector(endA.pos, endB.pos);
    
    Vector force = diff.copy().scale(K, K);
    
    if (diffstance < 0) {
      // forces go INWARDS 
      endA.addForce(force);
      endB.addForce(force.reverse());   
    } else {
      // forces go OUTWARDS
      endB.addForce(force);
      endA.addForce(force.reverse());
    }
  }
  
  public void render() {}
}
