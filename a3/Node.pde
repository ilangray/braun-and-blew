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
    println("adding force = " + f);
    netForce.add(f);
  }
  
  // f = m * a --> a = f / m
  public void updateAcceleration(float dt) {
    println("applying netforce = " + netForce);
    
    this.acc = netForce.scale(mass, mass).copy();
    
    // reset netForce for next time
    netForce.reset();
  }
  
  public void updateVelocity(float dt) {
    vel.add(acc.scale(dt, dt));
  }
  
  public void updatePosition(float dt) {
    Point prev = new Point(pos.x, pos.y);
    pos.add(acc.copy().scale(dt, dt));
    
    println("prev point = " + prev + ", new = " + pos);
  }
}
