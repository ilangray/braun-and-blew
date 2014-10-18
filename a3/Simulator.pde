// Runs the simulation
class Simulator {
  
  private static final float RESTING_ENERGY = 100;
  
  private final ArrayList<Node> nodes;
  private final ArrayList<Spring> springs;
  private final ArrayList<Zap> zaps;
  private final ArrayList<Damper> dampers;
  
  public Simulator(ArrayList<Node> nodes, ArrayList<Spring> springs, ArrayList<Zap> zaps, ArrayList<Damper> dampers) {
    this.nodes = nodes;
    this.springs = springs;
    this.zaps = zaps;
    this.dampers = dampers;
  } 
  
  // returns true if the system should be redrawn
  public boolean step(float dt) {
    aggregateForces();
    updatePositions(dt);
    
    println("ke = " + getKineticEnergy());
    return getKineticEnergy() > RESTING_ENERGY;
  }
  
  private void aggregateForces() {
    // tell all of the springs to apply their forces
    for (Spring s : springs) {
      s.applyForce(); 
    }
    
    // tell all the dampers to apply their forces
    for (Damper d : dampers) {
      d.applyForce(); 
    }
    
    // tell all the zaps to apply their forces
    for (Zap z : zaps) {
      z.applyForce(); 
    }
    
  }
  
  private float getKineticEnergy() {
    float total = 0;
    
    for (Node n : nodes) {
      total += n.getKineticEnergy(); 
    }
    
    return total;
  }
  
  // applies nodes' velocities to t
  private void updatePositions(float dt) {
    for (Node n : nodes) {
      n.updatePosition(dt);
    } 
  }
  
  
}
