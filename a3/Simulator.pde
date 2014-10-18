// Runs the simulation
class Simulator {
  
  private final ArrayList<Node> nodes;
  private final ArrayList<Spring> springs;
  
  public Simulator(ArrayList<Node> nodes, ArrayList<Spring> springs) {
    this.nodes = nodes;
    this.springs = springs;
  } 
  
  public void step(float dt) {
    aggregateForces();
    updateAccelerations(dt);
    updateVelocities(dt);
    updatePositions(dt);
  }
  
  private void aggregateForces() {
    // tell all of the springs to apply their forces
    for (Spring s : springs) {
      s.applyForce(); 
    }
    
    // tell all the dampers to apply their forces
    
    // tell all the zaps to apply their forces
  }
  
  private void updateAccelerations(float dt) {
    for (Node n : nodes) {
      n.updateAcceleration(dt); 
    }
  }
  
  private void updateVelocities(float dt) {
    for (Node n : nodes) {
      n.updateVelocity(dt); 
    }
  }
  
  // applies nodes' velocities to t
  private void updatePositions(float dt) {
    for (Node n : nodes) {
      n.updatePosition(dt);
    } 
  }
  
  
}
