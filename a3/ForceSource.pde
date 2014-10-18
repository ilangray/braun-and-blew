
// A ForceSource is something that can apply forces
interface ForceSource {
  // calculate the first applied by this source on its endpoints,
  // and update the nodes to reflect this force
  void applyForce();
}

/**
 * An InterNodeForce is a force that acts between two nodes.
 */
abstract class InterNodeForce implements ForceSource {
  public Node endA;
  public Node endB;
  
  public InterNodeForce(Node endA, Node endB) {
    this.endA = endA;
    this.endB = endB;
  }
  
  protected float getDistance() {
    return dist(endA.pos.x, endA.pos.y, endB.pos.x, endB.pos.y); 
  }
  
  // calculate the first applied by this source on its endpoints,
  // and update the nodes to reflect this force
  abstract void applyForce();
}
