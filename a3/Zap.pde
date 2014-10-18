// Instances of Coluomb laws
class Zap extends InterNodeForce {
 
  private static final float K = 10000;
  
  public Zap(Node endA, Node endB) {
    super(endA, endB);
  }
  
  public void applyForce() {
    float r = getDistance();
//    float mag = K / r;
    float mag = K * endA.mass * endB.mass / (r*r);
    
    Vector diff = new Vector(endA.pos, endB.pos);
    
    // normalize to extract direction, then scale by mag
    Vector force = diff.normalize().scale(mag, mag);
    
    endA.addForce(force);
    endB.addForce(force.reverse());
  }
}
