class Node {
  public final Point pos = new Point();
  public final Vector vel = new Vector();
  public final Vector acc = new Vector();
  public final Vector netForce = new Vector();
  
  public final int id;
  public final float mass;
  public final float radius;
  
  public boolean fixed = false;
  
  public Node(int id, float mass) {
    this.id = id;
    this.mass = mass;
    this.radius = sqrt(mass / PI);
  }
  
  public void addForce(Vector f) {}
  
  public void updateAcceleration(float dt) {
    
  }
  
  public void updateVelocity(float dt) {
    
  }
  
  public void updatePosition(float dt) {
    
  }
  
  public void render() {}
  
}
