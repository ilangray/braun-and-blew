class Node {
  public final Point pos = new Point();
  public final Vector vel = new Vector();
  
  private final Vector netForce = new Vector();
  private Vector acc;
  
  public final int id;
  public final float mass;
  public final float radius;
  
  public boolean fixed = false;
  
  public Node(int id, float mass) {
    this.id = id;
    this.mass = mass;
    this.radius = sqrt(mass / PI);
  }
  
  public void addForce(Vector f) {
//    println("adding force = " + f);
    netForce.add(f);
  }
  
  // f = m * a --> a = f / m
  private void updateAcceleration(float dt) {
    println("applying netforce = " + netForce);
    
    Vector prev = acc;
    
    float scale = 1.0f / mass;
    this.acc = netForce.copy().scale(scale, scale);
    
    println(" -- prev acc = " + prev + ", new = " + acc);
    
    // reset netForce for next time
    netForce.reset();
  }
  
  private void updateVelocity(float dt) {
    Vector prev = vel.copy();
    
    vel.add(acc.scale(dt, dt));
    
    println(" -- prev vel = " + prev + ", new = " + vel);
  }
  
  public void updatePosition(float dt) {
    if (fixed) { 
      return;
    }
    
    println("Node w/ id = " + id);
    
    updateAcceleration(dt);
    updateVelocity(dt);
    
    Point prev = new Point(pos.x, pos.y);
    pos.add(vel.copy().scale(dt, dt));
  
    println(" -- prev point = " + prev + ", new = " + pos);
  }
  
  public float getKineticEnergy() {
    float speed = vel.getMagnitude();
    return 0.5f * mass * speed*speed;   // 0.5 m * (v^2)
  }
}
