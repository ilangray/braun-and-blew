
/**
 * A Damper applies a force proportional to a 
 * node's velocity, in the opposite direction.
 */
class Damper implements ForceSource {

  private static final float K = 0.7f;

  private final Node node;

  public Damper(Node node) {
    this.node = node;
  }
    
  public void applyForce() {
    Vector velocity = node.vel.copy().scale(-K, -K);
    node.addForce(velocity);
  }
}
