// Instances of Coluomb laws
class Zap extends InterNodeForce {
 
  //private static final float K = 100000f;
   private static final float K = 1000000f;
  
  public Zap(Node endA, Node endB) {
    super(endA, endB);
  }
  
  public void applyForce() {
    float r = getDistance();
    
    // make sure r is always >= 1
    r = max(1, r);

    // compute the magnitude of the coulombs force
    float mag = K / (r*r);
    
    // normalize to extract direction, then scale by mag
    Vector force = new Vector(endA.pos, endB.pos).normalize().scale(mag, mag);
    
    // apply to end points
    endB.addForce(force);
    endA.addForce(force.reverse());
  }
}
