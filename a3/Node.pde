import java.lang.*;

class Node {
  public Point pos = new Point();
  public Vector vel = new Vector();
  
  private final Vector netForce = new Vector();
  private Vector acc;
  
  public final int id;
  public final float mass;
  public final float radius;
  
  public boolean fixed = false;
  
  public Node(int id, float mass) {
    this.id = id;
    this.mass = mass;
    this.radius = sqrt(mass / PI) * 10;
  }
  
  public void addForce(Vector f) {
//    println("adding force = " + f);
    netForce.add(f);
  }
  
  // f = m * a --> a = f / m
  private void updateAcceleration(float dt) {
//    println("applying netforce = " + netForce);
    
    Vector prev = acc;
    
    float scale = 1.0f / mass;
    this.acc = netForce.copy().scale(scale, scale);
    
//    println(" -- prev acc = " + prev + ", new = " + acc);
    
    // reset netForce for next time
    netForce.reset();
  }
  
  private void updateVelocity(float dt) {
    Vector prev = vel.copy();
    
    vel.add(acc.scale(dt, dt));
    
//    println(" -- prev vel = " + prev + ", new = " + vel);
  }
  
  /**
   * Hit tests a point against the node's position (radius/center)
   */
  public boolean containsPoint(int x, int y) {
    float dist = dist(pos.x, pos.y, x, y);
    return dist < radius;
  }
  
  public void updatePosition(float dt) {
    if (fixed) { 
      netForce.reset();  // Shouldn't accumulate forces if fixed
      return;
    }
    
//    println("Node w/ id = " + id);
    
    updateAcceleration(dt);
    updateVelocity(dt);
    
    Point prev = new Point(pos.x, pos.y);
    pos.add(vel.copy().scale(dt, dt));
    
    ensureInBounds();
    
   // println(" -- prev point = " + prev + ", new = " + pos);
  }
  
  private static final float COLLISION_SCALE = -0.8;
  
  private void ensureInBounds() {
    if (pos.x < radius) {
      pos.x = radius;
  
      vel.x *= COLLISION_SCALE;
    }
    else if (pos.x > width - radius) {
      pos.x = width - radius;   
         
      vel.x *= COLLISION_SCALE;
    }
    
    if (pos.y < radius) {
      pos.y = radius;
      vel.y *= COLLISION_SCALE;
    }
    else if (pos.y > height - radius) {
      pos.y = height - radius;
      vel.y *= COLLISION_SCALE;
    } 

    Float p1 = new Float(pos.x);
    Float p2 = new Float(pos.y);
    Float v1 = new Float(vel.x);
    Float v2 = new Float(vel.y);

    // If anything is NaN -- make new Point and Velocity
    if (p1.isNaN(p1) || p2.isNaN(p2) ||
        v1.isNaN(v1) || v2.isNaN(v2)) {
        pos = new Point(random(width), random(height));  // Place new point randomly
        vel = new Vector(0.0, 0.0);  // Start it out with no movement
    }
  }
  
  public float getKineticEnergy() {
    float speed = vel.getMagnitude();
    float ke = 0.5f * mass * speed*speed;
    return ke;   // 0.5 m * (v^2)
  }
}